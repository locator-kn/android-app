package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.view.fragments.ImageFragmentAdapter;
import com.locator_app.locator.view.map.MapsActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationDetailActivity extends FragmentActivity {

    @Bind(R.id.goBack)
    ImageView goBack;

    @Bind(R.id.locationTitle)
    TextView locationTitle;

    @Bind(R.id.showMap)
    ImageView showMap;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.impressions)
    RecyclerView impressions;

    LocatorLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);

        location = (LocatorLocation) getIntent().getSerializableExtra("location");

        viewPager.setAdapter(new ImageFragmentAdapter(getSupportFragmentManager()));

        setupLocationInformation();
    }

    @OnClick(R.id.goBack)
    void onGoBackClicked() {
        finish();
    }

    @OnClick(R.id.showMap)
    void onShowMapClicked() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    private void setupLocationInformation() {
        locationTitle.setText(location.title);
    }
}
