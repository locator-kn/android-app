package com.locator_app.locator.view.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.service.GpsService;
import com.locator_app.locator.view.UiError;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLng initialCameraPosition = null;
    private GoogleMap googleMap = null;
    private MapsController mapsController = null;

    @Bind(R.id.schoenHierButton)
    ImageView schoenHierButton;

    @Bind(R.id.viewOptionsButton)
    ImageView viewOptionsButton;
    private boolean isViewOptionsEnabled = false;

    @Bind(R.id.toggleHeatmapButton)
    ImageView toggleHeatmapButton;
    private boolean isHeatmapEnabled = true;
    public boolean isHeatmapEnabled() {  return isHeatmapEnabled; }

    @Bind(R.id.toggleLocationsButton)
    ImageView toggleLocationsButton;
    private boolean isLocationsEnabled = true;
    public boolean isLocationsEnabled() { return isLocationsEnabled; }

    @Bind(R.id.loadingSpinner)
    ImageView loadingSpinner;

    @Bind(R.id.loadingScreen)
    View loadingScreen;
    boolean loadingScreenVisible = true;

    GpsService gpsService;

    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        initLoadingSpinner();

        if (getIntent().hasExtra("lon") && getIntent().hasExtra("lat")) {
            double lon = getIntent().getDoubleExtra("lon", 0.0);
            double lat = getIntent().getDoubleExtra("lat", 0.0);
            initialCameraPosition = new LatLng(lat, lon);
        }

        gpsService = new GpsService(this);

        mapFragment = (MapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initLoadingSpinner() {
        Glide.with(this).load(R.drawable.preloader)
                .asGif()
                .into(loadingSpinner);
    }

    synchronized
    private void endLoadingScreen() {
        if (loadingScreenVisible) {
            loadingScreenVisible = false;
            if (loadingScreen.getVisibility() == View.VISIBLE) {
                YoYo.with(Techniques.TakingOff)
                        .duration(600)
                        .withListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                loadingScreen.setVisibility(View.GONE);
                            }
                        })
                        .playOn(loadingScreen);
            }
        }
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

    @OnClick(R.id.schoenHierButton)
    void onschoenHierButtonClick() {
        SchoenHierController.getInstance().markCurPosAsSchoenHier(gpsService)
                .map(response -> new LatLng(response.schoenHier.geoTag.getLatitude(),
                        response.schoenHier.geoTag.getLongitude()))
                .subscribe(
                        mapsController::addHeatpointAndRedraw,
                        (error) -> {
                            UiError.showError(this, error);
                        }
                );
    }

    @OnTouch(R.id.schoenHierButton)
    boolean onschoenHierButtonTouch(MotionEvent arg1) {
        if (arg1.getAction()== MotionEvent.ACTION_DOWN) {
            schoenHierButton.setAlpha((float) 0.6);
        }
        else if (arg1.getAction()==MotionEvent.ACTION_UP){
            schoenHierButton.setAlpha((float) 1);
        }
        return false;
    }

    @OnClick(R.id.viewOptionsButton)
    void onviewOptionsButtonClick() {
        setToggleButton(!isViewOptionsEnabled,
                viewOptionsButton,
                R.drawable.map_view_inverted,
                R.drawable.map_view);
        isViewOptionsEnabled = !isViewOptionsEnabled;

        enableToggleButtons(isViewOptionsEnabled);
    }

    private void enableToggleButtons(boolean enable) {
        toggleLocationsButton.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        toggleHeatmapButton  .setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    @OnClick(R.id.toggleHeatmapButton)
    void ontoggleHeatmapButtonClick() {
        setHeatmapEnabled(!isHeatmapEnabled);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    synchronized
    void setHeatmapEnabled(boolean enabled) {
        setToggleButton(enabled,
                toggleHeatmapButton,
                R.drawable.map_heatmap_inverted,
                R.drawable.map_heatmap);
        isHeatmapEnabled = enabled;

        if (enabled) {
            if (googleMap != null) {
                if (tileOverlay != null) {
                    tileOverlay.setVisible(true);
                }
                LatLng mapPos = googleMap.getCameraPosition().target;
                mapsController.redrawHeatMapAt(mapPos);
            }
        } else {
            if (tileOverlay != null) {
                tileOverlay.setVisible(false);
            }
        }
    }

    @OnClick(R.id.toggleLocationsButton)
    void ontoggleLocationsButtonClick() {
        setLocationsEnabled(!isLocationsEnabled);
    }

    synchronized
    private void setLocationsEnabled(boolean enabled) {
        setToggleButton(enabled,
                        toggleLocationsButton,
                        R.drawable.map_location_inverted,
                        R.drawable.map_location);
        isLocationsEnabled = enabled;

        if (mapsController != null) {
            if (enabled) {
                mapsController.setAllLocationsVisible();
                LatLng mapPos = googleMap.getCameraPosition().target;
                mapsController.redrawLocationsAt(mapPos);
            } else {
                mapsController.setAllLocationsInvisible();
            }
        }
    }

    private void setToggleButton(Boolean enabled, ImageView imageView, int enabledId, int disabledId) {
        imageView.setImageResource(enabled ? enabledId : disabledId);
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mapsController = new MapsController(this, googleMap);
        mapFragment.init(mapsController, googleMap);

        if (initialCameraPosition != null) {
            initiateMapByLatLon(initialCameraPosition);
        } else {
            gpsService.getCurLocation()
                    .subscribe(
                            this::initiateMap,
                            (err) -> {
                                endLoadingScreen();
                            }
                    );
        }
    }

    private void initiateMap(android.location.Location location) {
        LatLng locationPos = new LatLng(location.getLatitude(), location.getLongitude());
        initiateMapByLatLon(locationPos);
    }

    private void initiateMapByLatLon(LatLng locationPos) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        }
        gpsService.setMyLocationEnabled(googleMap);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPos, 15));

        mapsController.drawHeatMapAt(locationPos).subscribe(
                (res) -> {
                },
                (err) -> endLoadingScreen(),
                this::endLoadingScreen
        );
        mapsController.drawLocationsAt(locationPos).subscribe(
                (res) -> {
                },
                (err) -> endLoadingScreen(),
                this::endLoadingScreen
        );
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private HeatmapTileProvider heatmapTileProvider = null ;
    private TileOverlay tileOverlay = null;
    private int curNumberOfHeatPoints = 0;

    static private int[] colors = {
            Color.rgb(0, 255, 0),
            Color.rgb(255, 160, 0)
    };

    static private float[] startPoints = {
            (float) 0.1,
            (float) 0.8
    };

    synchronized
    public void drawHeatMap(List<LatLng> heatPoints) {
        if (heatPoints.isEmpty()) {
            return;
        }
        if (heatmapTileProvider != null && tileOverlay != null) {
            if (curNumberOfHeatPoints != heatPoints.size()) {
                heatmapTileProvider.setData(heatPoints);
                tileOverlay.clearTileCache();
                curNumberOfHeatPoints = heatPoints.size();
            }
            return;
        }
        curNumberOfHeatPoints = heatPoints.size();

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        heatmapTileProvider = new HeatmapTileProvider.Builder()
                .data(heatPoints)
                .radius(50)
//                .opacity(0.6)
//                .gradient(gradient)
                .build();
        tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
    }
}