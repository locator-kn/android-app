package com.locator_app.locator.view.locationcreation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.locator_app.locator.R;
import com.locator_app.locator.model.GoogleLocation;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.LoadingSpinner;
import com.locator_app.locator.view.home.HomeActivity;
import com.locator_app.locator.view.fragments.SearchResultsFragment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationSuggestions extends AppCompatActivity implements SearchResultsFragment.SearchInteractionListener {
    SearchResultsFragment searchResultsFragment;
    Bundle extras;
    GpsService gpsService;
    LoadingSpinner loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_suggestions);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        loadingSpinner = new LoadingSpinner(this);
        loadingSpinner.showSpinner();

        extras = getIntent().getExtras();

        searchResultsFragment = (SearchResultsFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.searchFragment);
        gpsService = new GpsService(this);
        gpsService.getCurLocation()
                .subscribe((location -> {
                    searchResultsFragment.search(location.getLongitude(),
                                                 location.getLatitude());
                }));
    }

    @OnClick(R.id.no)
    void onNoClicked() {
        Intent intent = new Intent(this, NameLocation.class);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLocationClicked(LocatorLocation location) {
        if (location instanceof GoogleLocation) {
            Intent intent = new Intent(this, ChooseCategories.class);
            intent.putExtras(extras);
            intent.putExtra("name", location.title);
            startActivity(intent);
        } else {
            // add image as impression; go to location detail view
        }
    }

    @Override
    public void onSearchResult(List<LocatorLocation> searchResult) {
        if (searchResult.isEmpty()) {
            Intent intent = new Intent(this, NameLocation.class);
            intent.putExtras(extras);
            startActivity(intent);
            this.finish();
        } else {
            loadingSpinner.hideSpinner();
        }
    }
}
