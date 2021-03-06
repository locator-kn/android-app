package com.locator_app.locator.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.ImageImpression;
import com.locator_app.locator.service.GpsService;
import com.locator_app.locator.util.AppTracker;
import com.locator_app.locator.view.fragments.ImageFragmentAdapter;
import com.locator_app.locator.view.home.HomeActivity;
import com.locator_app.locator.view.impressions.ImpressionController;
import com.locator_app.locator.view.impressions.ImpressionObserver;
import com.locator_app.locator.view.impressions.ImpressionRecyclerViewAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class LocationDetailActivity extends FragmentActivity implements ImpressionObserver {

    @Bind(R.id.goBack)
    ImageView goBack;

    @Bind(R.id.bubblescreen)
    ImageView bubblescreen;

    @Bind(R.id.locationTitle)
    TextView locationTitle;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.impressions)
    RecyclerView impressionsRecyclerView;

    @Bind(R.id.heart)
    ImageView heartImageView;

    @Bind(R.id.category1)
    ImageView category1;

    @Bind(R.id.category2)
    ImageView category2;

    ImageFragmentAdapter imageFragmentAdapter;
    ImpressionRecyclerViewAdapter impressionAdapter;

    GpsService gpsService;

    LocatorLocation location;
    List<AbstractImpression> impressions = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);
        bubblescreen.setEnabled(false);
        goBack.setEnabled(false);

        ImpressionController.addImpressionObserver(this);

        location = (LocatorLocation) getIntent().getSerializableExtra("location");

        imageFragmentAdapter = new ImageFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(imageFragmentAdapter);

        impressionAdapter = new ImpressionRecyclerViewAdapter(this);
        impressionAdapter.setLocation(location);

        impressionsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        impressionsRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), null));
        impressionsRecyclerView.setAdapter(impressionAdapter);

        gpsService = new GpsService(this);

        loadImpressions();
        setupLocationInformation();
    }

    @Override
    protected void onDestroy() {
        ImpressionController.removeImpressionObserver(this);
        super.onDestroy();
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
                .subscribe(
                        (result) -> {
                            location.favorites.add(UserController.getInstance().me().id);
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
                .subscribe(
                        (result) -> {
                            location.favorites.remove(UserController.getInstance().me().id);
                            impressionAdapter.updateFavorCounter();
                            updateFavorHeart();
                        },
                        (err) -> {
                            handleUnFavorError(err);
                        }
                );
    }

    private void handleUnFavorError(Throwable err) {
        UiError.showError(this, err, "uuups, das hat leider nicht geklappt :-/");
    }

    private void loadImpressions() {
        LocationController.getInstance().getImpressionsByLocationId(location.id)
                .doOnNext((val) -> AppTracker.getInstance().track("Locationview | load"))
                .toList()
                .subscribe(
                        this::handleImpressions,
                        (error) -> {
                            UiError.showError(this, error, "could not load impressions :-(");
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
                        (err) -> {
                            UiError.showError(this, err);
                        }
                );
    }

    @OnClick(R.id.goBack)
    void onGoBackClicked() {
        finish();
    }

    @OnClick(R.id.bubblescreen)
    void onShowMapClicked() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    private void setupLocationInformation() {
        locationTitle.setText(location.title);
        updateFavorHeart();
        showCategories();
    }

    private void updateFavorHeart() {
        int heartResourceId = userFavorsLocation() ? R.drawable.small_heart_red : R.drawable.small_heart_white;
        Glide.with(getApplicationContext())
                .load(heartResourceId)
                .dontAnimate()
                .into(heartImageView);
    }

    private static final Map<String, Integer> categoryToImage;
    static {
        Map<String, Integer> aMap = new HashMap<>();
        aMap.put("culture",   R.drawable.category_culture_color);
        aMap.put("gastro",    R.drawable.category_gastro_color);
        aMap.put("holiday",   R.drawable.category_holiday_color);
        aMap.put("nature",    R.drawable.category_nature_color);
        aMap.put("nightlife", R.drawable.category_party_color);
        aMap.put("secret",    R.drawable.category_secret_color);
        categoryToImage = Collections.unmodifiableMap(aMap);
    }

    private void showCategories() {
        if (location.categories.isEmpty()) {
            bubblescreen.setEnabled(true);
            goBack.setEnabled(true);
        } else if (location.categories.size() == 1) {
            loadCategory(category1, 0, 500);
        } else if (location.categories.size() == 2) {
            loadCategory(category2, 1, 500);
            loadCategory(category1, 0, 1200);
        }
    }

    private void loadCategory(ImageView v, int index, int delay) {
        String category = location.categories.get(index);
        v.setAlpha(0.f);
        if (categoryToImage.containsKey(category)) {
            Glide.with(this)
                    .load(categoryToImage.get(category))
                    .into(v);
            new Handler().postDelayed(() ->
                    YoYo.with(Techniques.RollIn)
                        .duration(1000)
                        .withListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                bubblescreen.setEnabled(true);
                                goBack.setEnabled(true);
                            }
                        })
                        .playOn(v),
                    delay);
        }
    }

    private boolean userFavorsLocation() {
        return UserController.getInstance().loggedIn() && location.favorites.contains(UserController.getInstance().me().id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        gpsService.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        gpsService.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onImpressionCreated(AbstractImpression.ImpressionType type) {
        Toast.makeText(this, "Deine Impression wurde hinzugefügt :)", Toast.LENGTH_SHORT).show();
        loadImpressions();
    }

    @Override
    public void onImpressionCreationFailed(AbstractImpression.ImpressionType type, Throwable error) {
        UiError.showError(this, error, "Deine Impression konnte leider nicht hochgeladen werden :(");
    }
}
