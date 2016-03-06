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
        startActivity(intent);
    }

    void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.welcome_to));
        customActionBar.setBackButtonVisibility(View.INVISIBLE);
        customActionBar.setCrossButtonVisibility(View.INVISIBLE);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
