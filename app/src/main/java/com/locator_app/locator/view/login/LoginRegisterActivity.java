package com.locator_app.locator.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.view.UiError;
import com.locator_app.locator.view.home.HomeActivity;
import com.locator_app.locator.view.register.RegisterNameActivity;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginRegisterActivity extends AppCompatActivity {

    @Bind(R.id.locator_logo)
    ImageView locatorLogo;
    @Bind(R.id.login)
    ImageView login;
    @Bind(R.id.login_facebook)
    ImageView loginFacebook;
    @Bind(R.id.register)
    ImageView register;

    private static CallbackManager callbackmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        ButterKnife.bind(this);
        setCustomActionBar();

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
    }

    @OnClick(R.id.login)
    public void onLoginClick() {
        Intent intent = new Intent(getApplicationContext(), LoginMailActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_facebook)
    public void onLoginFacebookClick() {
        facebookLogin();
    }

    @OnClick(R.id.register)
    public void onRegisterClick() {
        Intent intent = new Intent(getApplicationContext(), RegisterNameActivity.class);
        startActivity(intent);
    }

    private void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.welcome_to));
        customActionBar.setBackButtonVisibility(View.INVISIBLE);
        customActionBar.setCrossButtonJumpScreen(LoginRegisterStartActivity.class);
    }

    private void facebookLogin() {
        callbackmanager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                            UserController.getInstance()
                                .facebooklogin(loginResult.getAccessToken().getToken())
                                .subscribe(
                                        (user) -> {
                                            Intent intent = new Intent(LoginRegisterActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        },
                                        (error) -> {
                                            UiError.showError(LoginRegisterActivity.this,
                                                    error,
                                                    "Da ist was schiefgelaufen");
                                        }
                                );
                    }

                    @Override
                    public void onCancel() {
                        //user clicked on cancel button before login
                    }

                    @Override
                    public void onError(FacebookException error) {
                        UiError.showError(LoginRegisterActivity.this,
                                error,
                                "Da ist was schiefgelaufen");
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }
}
