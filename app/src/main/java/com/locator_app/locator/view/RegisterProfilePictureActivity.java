package com.locator_app.locator.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.service.users.RegistrationRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterProfilePictureActivity extends AppCompatActivity {

    @Bind(R.id.profilePicture)
    ImageView profilePicture;
    @Bind(R.id.profileNo)
    ImageView profileNo;

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

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
        HashMap<String, String> registerValues =
                (HashMap<String, String>)getIntent().getSerializableExtra("registerValues");
        //TODO: load image
        selectImage();
        registerValues.put("profilePicture", null);
        register(registerValues);
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

    private void register(HashMap<String, String> regValues) {
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
                    // du bist registriert und schon eingeloggt, toll
                },
                (error) -> {
                    // beim registrieren lief wohl was schief
                }
            );
    }

    private void loadImages() {
        //initialize components
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getApplicationContext());

        //set display image options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .build();

        //set images url's
        String urlProfile = "drawable://" + R.drawable.profile;
        String urlNo = "drawable://" + R.drawable.no;

        //display images
        universalImageLoader.displayImage(urlProfile, profilePicture, options);
        universalImageLoader.displayImage(urlNo, profileNo, options);
    }
}

