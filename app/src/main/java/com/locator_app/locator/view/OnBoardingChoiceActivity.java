package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;

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
        Glide.with(this).load(R.drawable.locator_logo).into(onboardingLocatorLogo);
        Glide.with(this).load(R.drawable.yes).into(onboardingYes);
        Glide.with(this).load(R.drawable.no).into(onboardingNo);
    }
}
