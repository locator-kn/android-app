package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        ButterKnife.bind(this);

        //set custom action bar
        setCustomActionBar();

        //load all images of the activity
        loadImages();
    }

    @OnClick(R.id.login)
    public void onLoginClick() {
        Intent intent = new Intent(getApplicationContext(), LoginMailActivity.class);
        startActivity(intent);
    }

    private void setCustomActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);
        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = (TextView) v.findViewById(R.id.actionbar_title);
        titleTxtView.setText(R.string.welcome_to);
    }

    private void loadImages() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getApplicationContext());

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
    }
}
