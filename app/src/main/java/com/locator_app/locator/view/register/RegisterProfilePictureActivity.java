package com.locator_app.locator.view.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.service.CameraService;
import com.locator_app.locator.util.BitmapHelper;
import com.locator_app.locator.view.LoadingSpinner;
import com.locator_app.locator.view.UiError;
import com.locator_app.locator.view.home.HomeActivity;
import com.locator_app.locator.view.login.LoginCustomActionBar;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterProfilePictureActivity extends AppCompatActivity {

    private static final int SELECT_FILE = 1;

    @Bind(R.id.profilePicture)
    ImageView profilePicture;
    @Bind(R.id.profileNo)
    ImageView profileNo;
    @Bind(R.id.profilePictureText)
    TextView profilePictureText;

    LoadingSpinner loadingSpinner;
    CameraService cameraService;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile_picture);
        ButterKnife.bind(this);

        cameraService = new CameraService(this);
        loadingSpinner = new LoadingSpinner(this);

        setCustomActionBar();
        loadImages();
    }

    @OnClick(R.id.profilePicture)
    public void onProfilePictureClick() {
        selectImage();
    }

    @OnClick(R.id.profileNo)
    public void onProfileNoClick() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.register));
        customActionBar.setBackButtonVisibility(View.GONE);
        customActionBar.setCrossButtonVisibility(View.GONE);
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
                    cameraService.takePhoto().subscribe(
                            (uri) -> {
                                RegisterProfilePictureActivity.this.uri = uri;
                            },
                            (err) -> {
                            }
                    );
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
            if (requestCode == CameraService.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                Uri profilePictureUri = this.uri;
                uploadProfilePicture(profilePictureUri);
            } else if (requestCode == SELECT_FILE) {
                Uri profilePictureUri = data.getData();
                uploadProfilePicture(profilePictureUri);
            }
        }
    }

    private void uploadProfilePicture(Uri uri) {
        try {
            Bitmap profilePicture = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            uploadProfilePicture(profilePicture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadProfilePicture(Bitmap profilePicture) {
        loadingSpinner.showSpinner();
        UserController.getInstance().setProfilePicture(profilePicture)
                .subscribe(
                        (res) -> {
                            Bitmap roundBitmap = BitmapHelper.getRoundBitmap(profilePicture, this.profilePicture.getWidth());
                            this.profilePicture.setImageBitmap(roundBitmap);
                            profilePictureText.setText(getResources().getString(R.string.your_profile_picture));
                            loadingSpinner.hideSpinner();
                            Glide.with(this).load(R.drawable.continue_white).into(profileNo);
                            reloadUserProfilePictureFromServer();
                        },
                        (error) -> {
                            loadingSpinner.hideSpinner();
                            UiError.showError(this, error, "Da ist was schief gelaufen");
                        }
                );
    }

    private void reloadUserProfilePictureFromServer() {
        // request current user to update the path to the profile image
        if (UserController.getInstance().loggedIn()) {
            UserController.getInstance().checkProtected()
                    .subscribe(
                            (val) -> {},
                            (err) -> {}
                    );
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void loadImages() {
        Glide.with(this).load(R.drawable.profile).into(profilePicture);
        Glide.with(this).load(R.drawable.no).into(profileNo);
    }
}

