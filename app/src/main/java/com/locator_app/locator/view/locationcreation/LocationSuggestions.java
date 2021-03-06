package com.locator_app.locator.view.locationcreation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.model.GoogleLocation;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.service.GpsService;
import com.locator_app.locator.view.LoadingSpinner;
import com.locator_app.locator.view.LocationDetailActivity;
import com.locator_app.locator.view.UiError;
import com.locator_app.locator.view.home.HomeActivity;
import com.locator_app.locator.view.fragments.SearchResultsFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationSuggestions extends FragmentActivity implements SearchResultsFragment.SearchInteractionListener {

    SearchResultsFragment searchResultsFragment;
    Bundle extras;
    GpsService gpsService;

    @Bind(R.id.uploadLoadingSpinner)
    ImageView uploadSpinnerView;

    @Bind(R.id.searchLoadingSpinner)
    ImageView searchSpinnerView;

    @Bind(R.id.content)
    View fadeOutLayout;

    @Bind(R.id.cancelButton)
    ImageView cancelButton;

    boolean hasCoordiantes = false;

    LoadingSpinner searchLoadingSpinner;
    LoadingSpinner uploadLoadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_suggestions);
        ButterKnife.bind(this);

        uploadLoadingSpinner = new LoadingSpinner(this, uploadSpinnerView, fadeOutLayout);
        searchLoadingSpinner = new LoadingSpinner(this, searchSpinnerView);
        searchLoadingSpinner.showSpinner();

        extras = getIntent().getExtras();

        searchResultsFragment = (SearchResultsFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.searchFragment);
        gpsService = new GpsService(this);
        gpsService.getCurLocation()
                .subscribe(
                        (location) -> {
                            hasCoordiantes = true;
                            extras.putDouble("lon", location.getLongitude());
                            extras.putDouble("lat", location.getLatitude());
                            searchResultsFragment.search(location.getLongitude(),
                                    location.getLatitude());
                        },
                        (err) -> {
                            Toast.makeText(this, "Versuch es nochmal mit GPS", Toast.LENGTH_SHORT).show();
                            LocationSuggestions.this.finish();
                        }
                );
    }

    @OnClick(R.id.no)
    void onNoClicked() {
        if (hasCoordiantes) {
            Intent intent = new Intent(this, NameLocation.class);
            intent.putExtras(extras);
            startActivityForResult(intent, LocationCreationController.LOCATION_CREATION_REQUEST);
        } else {
            Toast.makeText(this, "Versuch es nochmal mit GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLocationClicked(LocatorLocation location) {
        if (location instanceof GoogleLocation) {
            Intent intent = new Intent(this, NameLocation.class);
            extras.putString("name_hint", location.title);
            intent.putExtras(extras);
            startActivityForResult(intent, LocationCreationController.LOCATION_CREATION_REQUEST);
        } else {
            uploadLoadingSpinner.showSpinner();
            cancelButton.setVisibility(View.GONE);
            Bitmap imageBitmap;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), (Uri) extras.get("imageUri"));
            } catch (Exception e) {
                Toast.makeText(this, "Could not find Image file",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            LocationController.getInstance().createImageImpression(location.id, imageBitmap)
                    .subscribe(
                            (val) -> {
                                Intent intent = new Intent(this, LocationDetailActivity.class);
                                intent.putExtra("location", location);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                uploadLoadingSpinner.hideSpinner();
                                cancelButton.setVisibility(View.VISIBLE);
                                this.finish();
                            },
                            (err) -> {
                                uploadLoadingSpinner.hideSpinner();
                                cancelButton.setVisibility(View.VISIBLE);
                                UiError.showError(LocatorApplication.getAppContext(),
                                        err,
                                        "Deine Impression konnte leider nicht hochgeladen werden :-(");
                            }
                    );
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
            searchResult.add(searchResult.size(), new SearchResultsFragment.DummyLocation());
            searchLoadingSpinner.hideSpinner();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (resultCode == LocationCreationController.LOCATION_CREATED) {
            super.onActivityResult(requestCode, resultCode, intent);
            finish();
        }
        gpsService.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        gpsService.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
