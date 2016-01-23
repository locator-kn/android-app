package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.ImageImpression;
import com.locator_app.locator.view.fragments.ImageFragmentAdapter;
import com.locator_app.locator.view.map.MapsActivity;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    RecyclerView impressionsRecyclerView;

    ImageFragmentAdapter imageFragmentAdapter;

    LocatorLocation location;
    List<AbstractImpression> impressions = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);

        location = (LocatorLocation) getIntent().getSerializableExtra("location");

        loadImpressions();
        setupLocationInformation();
    }

    private void loadImpressions() {
        LocationController.getInstance().getImpressionsByLocationId(location.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(
                        this::handleImpressions,
                        (error) -> {
                            Toast.makeText(getApplicationContext(), "could not load impressions :-(",
                                    Toast.LENGTH_SHORT).show();
                        }
                );
    }

    private void handleImpressions(List<AbstractImpression> impressions) {
        this.impressions = impressions;
        Observable.from(impressions)
                .filter(impression -> impression.type() == AbstractImpression.ImpressionType.IMAGE)
                .map(impression -> (ImageImpression)impression)
                .map(ImageImpression::getImageUri)
                .toList()
                .subscribe(
                        (imageImpressions) -> {
                            imageFragmentAdapter = new ImageFragmentAdapter(getSupportFragmentManager());
                            List<String> images = new LinkedList<>();
                            images.add(location.images.getNormal());
                            images.addAll(imageImpressions);
                            imageFragmentAdapter.setImages(images);
                            viewPager.setAdapter(imageFragmentAdapter);
                        }
                );
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
