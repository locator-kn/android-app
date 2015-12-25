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

public class LoginRegisterStartActivity extends AppCompatActivity {

    private ImageView locatorLogo;
    private ImageView loginYes;
    private ImageView loginNo;
    private UniversalImageLoader universalImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register_start);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom);

        //set action bar title
        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = (TextView) v.findViewById(R.id.actionbar_title);
        titleTxtView.setText(R.string.welcome_to);

        //TODO: check if already authenticated
        //TODO: getDeviceId

        //initialize components
        universalImageLoader = new UniversalImageLoader(getApplicationContext());
        locatorLogo = (ImageView) findViewById(R.id.locator_logo);
        loginYes = (ImageView) findViewById(R.id.login_yes);
        loginNo = (ImageView) findViewById(R.id.login_no);

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

        loginYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginRegisterActivity.class);
                startActivity(intent);
            }
        });

        loginNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
