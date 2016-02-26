package com.locator_app.locator.view.locationcreation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.GoogleLocation;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.HomeActivity;
import com.locator_app.locator.view.fragments.SearchResultsFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseCategories extends Activity {
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_categories);
        ButterKnife.bind(this);

        extras = getIntent().getExtras();
    }

    @OnClick(R.id.culture)
    void onCultureClicked() {
        Toast.makeText(this, "culture clicked", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.next)
    void onNextClicked() {
        Intent intent = new Intent(this, NameLocation.class);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
