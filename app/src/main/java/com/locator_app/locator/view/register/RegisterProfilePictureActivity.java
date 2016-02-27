package com.locator_app.locator.view.register;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.users.RegistrationRequest;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.util.BitmapHelper;
import com.locator_app.locator.view.OnBoardingChoiceActivity;
import com.locator_app.locator.view.login.LoginCustomActionBar;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterProfilePictureActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int PIC_CROP = 3;

    @Bind(R.id.profilePicture)
    ImageView profilePicture;
    @Bind(R.id.profileNo)
    ImageView profileNo;
    @Bind(R.id.profilePictureText)
    TextView profilePictureText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile_picture);
        ButterKnife.bind(this);
        setCustomActionBar();
        loadImages();
    }

    @OnClick(R.id.profilePicture)
    public void onProfilePictureClick() {
        selectImage();
    }

    @OnClick(R.id.profileNo)
    public void onProfileNoClick() {
        HashMap<String, String> registerValues =
                (HashMap<String, String>)getIntent().getSerializableExtra("registerValues");
        registerValues.put("profilePicture", null);
        register(registerValues);
    }

    private void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.register));
        customActionBar.setCrossButtonJumpScreen(LoginRegisterStartActivity.class);
        customActionBar.setColor(R.color.colorRegister);
    }

    private void selectImage() {
        String takePhoto = getResources().getString(R.string.take_photo);
        String choosePhoto = getResources().getString(R.string.choose_photo);
        String cancel = getResources().getString(R.string.cancel);
        final CharSequence[] items = { takePhoto, choosePhoto, cancel};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterProfilePictureActivity.this);
        builder.setTitle(getResources().getString(R.string.choose_profile_picture));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(takePhoto)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals(choosePhoto)) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, getResources().getString(R.string.select_file)),
                            SELECT_FILE);
                } else if (items[item].equals(cancel)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                //save image
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                cropImage(data.getData());

            } else if (requestCode == SELECT_FILE) {
                cropImage(data.getData());
            }
            else if(requestCode == PIC_CROP) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                Bitmap roundedBitmap = BitmapHelper.getRoundBitmap(thePic, 500);
                profilePicture.setImageBitmap(roundedBitmap);
                profilePictureText.setText(getResources().getString(R.string.your_profile_picture));
                //TODO: replace "no"-image with "continue"-image or something
            }
        }
    }

    private void cropImage(Uri selectedImageUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(selectedImageUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            cropIntent.putExtra("return-data", true);
            cropIntent.putExtra("circleCrop", new String(""));
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void register(HashMap<String, String> regValues) {
        final Context context = getApplicationContext();
        HashMap<String, String> registerValues = regValues;
        UserController controller = UserController.getInstance();
        RegistrationRequest request = new RegistrationRequest();
        request.mail = registerValues.get("mail");
        request.name = registerValues.get("name");
        request.password = registerValues.get("password");
        request.residence = registerValues.get("residence");
        controller.register(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                (loginResponse) -> {
                    Intent intent = new Intent(context, OnBoardingChoiceActivity.class);
                    intent.putExtra("name", loginResponse.name);
                    startActivity(intent);
                },
                (error) -> {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, RegisterNameActivity.class);
                    startActivity(intent);
                }
            );
    }

    private void loadImages() {
        Glide.with(this).load(R.drawable.profile).into(profilePicture);
        Glide.with(this).load(R.drawable.no).into(profileNo);
    }
}

