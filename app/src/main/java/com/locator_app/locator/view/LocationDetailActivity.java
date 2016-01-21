package com.locator_app.locator.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LocationDetailActivity extends Activity {


    @Bind(R.id.locationTitle)
    TextView locationTitle;

    LocatorLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);

        location = (LocatorLocation) getIntent().getSerializableExtra("location");

        setupLocationInformation();
    }

    private void setupLocationInformation() {
        locationTitle.setText(location.title);
    }
}
