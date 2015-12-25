package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.service.BitmapWorkerTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class LoginRegisterActivity extends AppCompatActivity {

    private ImageView locatorLogo;
    private ImageView login;
    private ImageView loginFacebook;
    private ImageView register;
    private UniversalImageLoader universalImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);

        //set action bar title
        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = (TextView) v.findViewById(R.id.actionbar_title);
        titleTxtView.setText(R.string.welcome_to);

        //initialize components
        universalImageLoader = new UniversalImageLoader(getApplicationContext());
        locatorLogo = (ImageView) findViewById(R.id.locator_logo);
        login = (ImageView) findViewById(R.id.login);
        loginFacebook = (ImageView) findViewById(R.id.login_facebook);
        register = (ImageView) findViewById(R.id.register);

        //set display image options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .build();

        //set images url's
        String urlLocator = "drawable://" + R.drawable.locator_logo;
        String urlLogin = "drawable://" + R.drawable.login;
        String urlLoginFacebook = "drawable://" + R.drawable.login_facebook;
        String urlRegister= "drawable://" + R.drawable.register;

        //display images
        universalImageLoader.displayImage(urlLocator, locatorLogo, options);
        universalImageLoader.displayImage(urlLogin, login, options);
        universalImageLoader.displayImage(urlLoginFacebook, loginFacebook, options);
        universalImageLoader.displayImage(urlRegister, register, options);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginMailActivity.class);
                startActivity(intent);
            }
        });
    }
}
