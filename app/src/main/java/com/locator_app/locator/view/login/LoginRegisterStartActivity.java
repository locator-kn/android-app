package com.locator_app.locator.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.DeviceController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.service.RegistrationIntentService;
import com.locator_app.locator.view.home.HomeActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginRegisterStartActivity extends AppCompatActivity {

    @Bind(R.id.locator_logo)
    ImageView locatorLogo;

    @Bind(R.id.login_yes)
    ImageView loginYes;

    @Bind(R.id.login_no)
    ImageView loginNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register_start);
        ButterKnife.bind(this);
        setCustomActionBar();
        setupEventBus();
        loadImages();
        registerDeviceAndLogin();
    }

    private void registerDeviceAndLogin() {
        if (!DeviceController.getInstance().isDeviceAlreadyRegistered()) {
            Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
            startService(intent);
        } else {
            UserController.getInstance()
                    .logInLastLoggedInUser()
                    .subscribe(
                            (loginResponse -> jumpToHomeScreen()),
                            (err) -> {
                            }
                    );
        }
    }

    private void setupEventBus() {
        // todo, configure event bus
        // internet on off
        // gps on off
        // user logged in / out
        // what else?
    }

    @OnClick(R.id.login_yes)
    void onLoginYesClicked() {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @OnClick(R.id.login_no)
    void onLoginNoClicked() {
        jumpToHomeScreen();
    }

    private void jumpToHomeScreen() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.welcome_to));
        customActionBar.setBackButtonVisibility(View.INVISIBLE);
        customActionBar.setCrossButtonVisibility(View.INVISIBLE);
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.actionbar_custom);
//        View v = getSupportActionBar().getCustomView();
//        TextView titleTxtView = (TextView) v.findViewById(R.id.actionbar_title);
//        titleTxtView.setText(R.string.welcome_to);
//        ImageView backButton = (ImageView) v.findViewById(R.id.actionbar_back);
//        backButton.setVisibility(View.INVISIBLE);
//        ImageView crossButton = (ImageView) v.findViewById(R.id.actionbar_cross);
//        crossButton.setVisibility(View.INVISIBLE);
    }

    void loadImages() {
        Glide.with(this).load(R.drawable.locator_logo).into(locatorLogo);
        Glide.with(this).load(R.drawable.yes).into(loginYes);
        Glide.with(this).load(R.drawable.no).into(loginNo);
    }
}
