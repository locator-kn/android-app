package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.db.Couch;
import com.locator_app.locator.util.CacheImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnBoardingChoiceActivity extends AppCompatActivity {

    @Bind(R.id.onboarding_locator_logo)
    ImageView onboardingLocatorLogo;

    @Bind(R.id.onboarding_yes)
    ImageView onboardingYes;

    @Bind(R.id.onboarding_no)
    ImageView onboardingNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_choice);
        ButterKnife.bind(this);
        setCustomActionBar(getIntent().getStringExtra("name"));

        loadImages();
    }

    @OnClick(R.id.onboarding_yes)
    void onLoginYesClicked() {
        //TODO: Onboarding
    }

    @OnClick(R.id.onboarding_no)
    void onLoginNoClicked() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void setCustomActionBar(String name) {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle("Hi " + name + "! " + getResources().getString(R.string.welcome_to));
        customActionBar.setBackButtonVisibility(View.INVISIBLE);
        customActionBar.setCrossButtonVisibility(View.INVISIBLE);
    }

    void loadImages() {

        //set images url's
        String urlLocator = "drawable://" + R.drawable.locator_logo;
        String urlYes = "drawable://" + R.drawable.yes;
        String urlNo = "drawable://" + R.drawable.no;

        CacheImageLoader.getInstance().setImage(urlLocator, onboardingLocatorLogo);
        CacheImageLoader.getInstance().setImage(urlYes, onboardingYes);
        CacheImageLoader.getInstance().setImage(urlNo, onboardingNo);
    }
}
