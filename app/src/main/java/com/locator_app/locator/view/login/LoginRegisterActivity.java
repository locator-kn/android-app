package com.locator_app.locator.view.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.errorhandling.HttpError;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.view.LocatorHeader;
import com.locator_app.locator.view.UiError;
import com.locator_app.locator.view.home.HomeActivity;
import com.locator_app.locator.view.register.RegisterNameActivity;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginRegisterActivity extends Activity {

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
        setContentView(R.layout.activity_register_login);
        ButterKnife.bind(this);

        LocatorHeader header = new LocatorHeader(this);
        header.setTitle(R.string.welcome_to);

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
                                            if (error instanceof HttpError &&
                                                    ((HttpError) error).getErrorCode() == HttpError.HttpErrorCode.conflict) {
                                                UiError.showError(LoginRegisterActivity.this, error, "Diese E-Mail ist bereits registriert");
                                            } else {
                                                UiError.showError(LoginRegisterActivity.this,
                                                        error,
                                                        "Da ist was schiefgelaufen");
                                            }
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
