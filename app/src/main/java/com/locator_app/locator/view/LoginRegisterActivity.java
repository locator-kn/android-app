package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.locator_app.locator.R;
import com.locator_app.locator.util.CacheImageLoader;
import com.facebook.FacebookSdk;

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
        loadImages();

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

    private void loadImages() {
        //set images url's
        String urlLocator = "drawable://" + R.drawable.locator_logo;
        String urlLogin = "drawable://" + R.drawable.login;
        String urlLoginFacebook = "drawable://" + R.drawable.login_facebook;
        String urlRegister= "drawable://" + R.drawable.register;

        CacheImageLoader.getInstance().setImage(urlLocator, locatorLogo);
        CacheImageLoader.getInstance().setImage(urlLogin, login);
        CacheImageLoader.getInstance().setImage(urlLoginFacebook, loginFacebook);
        CacheImageLoader.getInstance().setImage(urlRegister, register);
    }

    private void facebookLogin() {
        callbackmanager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //TODO: send token to server
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT);
                                        } else {
//                                            String jsonresult = String.valueOf(json);
                                            String firstName = json.optString("first_name");
                                            Toast.makeText(getApplicationContext(), "Hallo " + firstName, Toast.LENGTH_LONG);
                                            //TODO: go to next activity
                                        }
                                    }

                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name, last_name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        //user clicked on cancel button before login
                        finish();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT);
                        Log.d("Facebook Login", error.toString());
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }
}
