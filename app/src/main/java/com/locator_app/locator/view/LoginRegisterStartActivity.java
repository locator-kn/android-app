package com.locator_app.locator.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.locator_app.locator.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

        //set custom action bar
        setCustomActionBar();

        //load all images of the activity
        loadImages();

        //TODO: check if already authenticated
        //TODO: getDeviceId
    }

    //events
    @OnClick(R.id.login_yes)
    public void loginYesClick() {
        Intent intent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_no)
    public void loginNoClick() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
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
        String urlYes = "drawable://" + R.drawable.yes;
        String urlNo = "drawable://" + R.drawable.no;

        //display images
        universalImageLoader.displayImage(urlLocator, locatorLogo, options);
        universalImageLoader.displayImage(urlYes, loginYes, options);
        universalImageLoader.displayImage(urlNo, loginNo, options);
    }
}
