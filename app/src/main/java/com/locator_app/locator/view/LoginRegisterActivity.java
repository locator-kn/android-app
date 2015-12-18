package com.locator_app.locator.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.locator_app.locator.R;
import com.locator_app.locator.service.BitmapWorkerTask;

public class LoginRegisterActivity extends AppCompatActivity {

    private ImageView locatorLogo;
    private ImageView login;
    private ImageView loginFacebook;
    private ImageView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);

        locatorLogo = (ImageView) findViewById(R.id.locator_logo);
        login = (ImageView) findViewById(R.id.login);
        loginFacebook = (ImageView) findViewById(R.id.login_facebook);
        register = (ImageView) findViewById(R.id.register);
        loadBitmap(R.drawable.locator_logo, locatorLogo, 500, 300);
        loadBitmap(R.drawable.login, login, 200, 170);
        loadBitmap(R.drawable.login_facebook, loginFacebook, 200, 170);
        loadBitmap(R.drawable.register, register, 200, 170);
    }

    //Load every image in separate thread
    public void loadBitmap(int resId, ImageView imageView, int width, int height) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, getResources());
        task.execute(resId, width, height);
    }

}
