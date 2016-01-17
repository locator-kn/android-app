package com.locator_app.locator.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.User;
import com.locator_app.locator.db.Couch;
import com.locator_app.locator.util.CacheImageLoader;
import com.locator_app.locator.util.RxBus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import javax.security.auth.callback.CallbackHandler;

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
        setCustomActionBar();

        setupEventBus();

        loadImages();

        UserController userController = UserController.getInstance();
        Couch.get().onAppStart(userController.me());
        if (userController.me().loggedIn)  {
            Toast.makeText(getApplicationContext(), "Welcome back " + userController.me().name,
                    Toast.LENGTH_LONG).show();
            jumpToHomeScreen();
        }

        //TODO: getDeviceId
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.welcome_to));
        customActionBar.setBackButtonVisibility(View.INVISIBLE);
        customActionBar.setCrossButtonVisibility(View.INVISIBLE);
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.actionbar_custom);
//        View v = getSupportActionBar().getCustomView();
//        TextView titleTxtView = (TextView) v.findViewById(R.id.actionbar_title);
//        titleTxtView.setText(R.string.welcome_to);
//        ImageView backButton = (ImageView) v.findViewById(R.id.actionbar_back);
//        backButton.setVisibility(View.INVISIBLE);
//        ImageView crossButton = (ImageView) v.findViewById(R.id.actionbar_cross);
//        crossButton.setVisibility(View.INVISIBLE);
    }

    void loadImages() {

        //set images url's
        String urlLocator = "drawable://" + R.drawable.locator_logo;
        String urlYes = "drawable://" + R.drawable.yes;
        String urlNo = "drawable://" + R.drawable.no;

        CacheImageLoader.getInstance().setImage(urlLocator, locatorLogo);
        CacheImageLoader.getInstance().setImage(urlYes, loginYes);
        CacheImageLoader.getInstance().setImage(urlNo, loginNo);
    }
}
