package com.locator_app.locator.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.util.CacheImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FacebookLoginPasswordActivity extends AppCompatActivity {

    @Bind(R.id.facebookLoginPassword)
    TextView facebookLoginPassword;
    @Bind(R.id.facebookLogo)
    ImageView facebookLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login_password);
        ButterKnife.bind(this);
        setCustomActionBar();
        loadImages();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        facebookLoginPassword.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String mail = getIntent().getStringExtra("mail");
                String password = facebookLoginPassword.getText().toString();
                //TODO:facebook login
                //login(mail, password);
                return true;
            }
            return false;
        });
    }

    private void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.login));
        customActionBar.setCrossButtonJumpScreen(LoginRegisterStartActivity.class);
        customActionBar.setColor(R.color.colorFacebook);
    }

    //TODO:
    void loginFacebook(String mail, String password) {
//        final Context context = getApplicationContext();
//
//        UserController userController = UserController.getInstance();
//        LoginRequest loginRequest = new LoginRequest();
//        loginRequest.mail = mail;
//        loginRequest.password = password;
//        userController.login(loginRequest)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        (loginResponse) -> {
//                            Toast.makeText(context, "Hi " + loginResponse.name, Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(context, HomeActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                        },
//                        (error) -> {
//                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            LoginPasswordActivity.this.finish();
//                        });
    }

    private void loadImages() {
        //set images url's
        String urlFacebookLogo = "drawable://" + R.drawable.facebook_logo;
        CacheImageLoader.getInstance().loadAsync(urlFacebookLogo)
                .subscribe(
                        (bitmap -> facebookLogo.setImageBitmap(bitmap)),
                        (error -> {})
                );
    }
}

