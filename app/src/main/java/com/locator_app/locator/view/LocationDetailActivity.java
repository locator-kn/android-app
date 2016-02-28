package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.ImageImpression;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.fragments.ImageFragmentAdapter;
import com.locator_app.locator.view.home.HomeActivity;
import com.locator_app.locator.view.impressions.ImpressionRecyclerViewAdapter;

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

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.impressions)
    RecyclerView impressionsRecyclerView;

    @Bind(R.id.heart)
    ImageView heartImageView;

    ImageFragmentAdapter imageFragmentAdapter;
    ImpressionRecyclerViewAdapter impressionAdapter;

    LocatorLocation location;
    List<AbstractImpression> impressions = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);

        location = (LocatorLocation) getIntent().getSerializableExtra("location");

        imageFragmentAdapter = new ImageFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(imageFragmentAdapter);

        impressionAdapter = new ImpressionRecyclerViewAdapter();
        impressionAdapter.setLocation(location);

        impressionsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        impressionsRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), null));
        impressionsRecyclerView.setAdapter(impressionAdapter);

        loadImpressions();
        setupLocationInformation();
    }

    @OnClick(R.id.heart)
    void onHeartClick() {
        if (UserController.getInstance().loggedIn()) {
            if (userFavorsLocation()) {
                doUnfavorLocation();
            } else {
                doFavorLocation();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Hierfür musst du dich einloggen :-)",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void doFavorLocation() {
        LocationController.getInstance().favorLocation(location.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (result) -> {
                            location.favorites.add(UserController.getInstance().me()._id);
                            impressionAdapter.updateFavorCounter();
                            updateFavorHeart();
                        },
                        (err) -> {
                            handleUnFavorError(err);
                        }
                );
    }

    private void doUnfavorLocation() {
        LocationController.getInstance().unfavorLocation(location.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (result) -> {
                            location.favorites.remove(UserController.getInstance().me()._id);
                            impressionAdapter.updateFavorCounter();
                            updateFavorHeart();
                        },
                        (err) -> {
                            handleUnFavorError(err);
                        }
                );
    }

    private void handleUnFavorError(Throwable err) {
        Toast.makeText(getApplicationContext(), "uuups, das hat leider nicht geklappt :-/",
                Toast.LENGTH_SHORT).show();
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
        impressionAdapter.setImpressions(impressions);
        loadImageImpressionsToImageFragmentAdapter();
    }

    private void loadImageImpressionsToImageFragmentAdapter() {
        Observable<String> imageImpressionUris =
                Observable.from(impressions)
                        .filter(impression -> impression.type() == AbstractImpression.ImpressionType.IMAGE)
                        .map(impression -> (ImageImpression) impression)
                        .map(ImageImpression::getImageUri);
        Observable.just(location.images.getNormal())
                .mergeWith(imageImpressionUris)
                .toList()
                .subscribe(
                        imageFragmentAdapter::setImages,
                        (err) -> { }
                );
    }

    @OnClick(R.id.goBack)
    void onGoBackClicked() {
        finish();
    }

    @OnClick(R.id.bubblescreen)
    void onShowMapClicked() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setupLocationInformation() {
        locationTitle.setText(location.title);
        showDistanceToLocation();
        updateFavorHeart();
    }

    private void updateFavorHeart() {
        int heartResourceId = userFavorsLocation() ? R.drawable.small_heart_red : R.drawable.small_heart_white;
        Glide.with(getApplicationContext()).load(heartResourceId)
                .into(heartImageView);
    }

    private boolean userFavorsLocation() {
        return location.favorites.contains(UserController.getInstance().me()._id);
    }

    private void showDistanceToLocation() {
        GpsService service = new GpsService(this);
        service.getCurLocation()
                .subscribe(
                        (location) -> {
                            //double distance = DistanceCalculator.distanceInKm();
                            //double distanceToLocation = DistanceCalculator.
                        },
                        (error) -> {

                        }
                );
    }
}
