package com.locator_app.locator.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.service.users.LoginRequest;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile_picture);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);

        //set action bar title and color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable
                (ContextCompat.getColor(getApplicationContext(), R.color.colorRegister)));
        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = (TextView) v.findViewById(R.id.actionbar_title);
        titleTxtView.setText(R.string.register);

        loadImages();

        /*loginPassword.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String mail = getIntent().getStringExtra("mail");
                String password = loginPassword.getText().toString();
                login(mail, password);
                return true;
            }
            return false;
        });*/
    }

    @OnClick(R.id.profilePicture)
    public void onProfilePictureClick() {
        HashMap<String, String> registerValues =
                (HashMap<String, String>)getIntent().getSerializableExtra("registerValues");
        //TODO: load image
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

    void login(String mail, String password) {
        /*final Context context = getApplicationContext();

        UserController userController = UserController.getInstance();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.mail = mail;
        loginRequest.password = password;
        userController.login(loginRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (loginResponse) -> {
                            Toast.makeText(context, "Hi " + loginResponse.name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        },
                        (error) -> {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                            LoginPasswordActivity.this.finish();
                        });*/
    }
}

