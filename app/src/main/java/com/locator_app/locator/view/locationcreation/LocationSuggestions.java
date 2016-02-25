package com.locator_app.locator.view.locationcreation;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.HomeActivity;
import com.locator_app.locator.view.LoginCustomActionBar;
import com.locator_app.locator.view.fragments.SearchResultsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationSuggestions extends AppCompatActivity {
    SearchResultsFragment searchResultsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_suggestions);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        searchResultsFragment = (SearchResultsFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.searchFragment);
        searchResultsFragment.search("Hotel", 9.169753789901733, 47.66868204997508);
    }

    @OnClick(R.id.no)
    void onNoClicked() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
