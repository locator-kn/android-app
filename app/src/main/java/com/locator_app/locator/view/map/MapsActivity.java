package com.locator_app.locator.view.map;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.util.GpsService;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import rx.Subscription;

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

    GpsService gpsService;

    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        
        if (getIntent().hasExtra("lon") && getIntent().hasExtra("lat")) {
            double lon = getIntent().getDoubleExtra("lon", 0.0);
            double lat = getIntent().getDoubleExtra("lat", 0.0);
            initialCameraPosition = new LatLng(lat, lon);
        }

        Glide.with(this).load(R.drawable.profile).asBitmap().override(60, 60).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                MapsActivity.this.currentPos = resource;
            }
        });

        gpsService = (GpsService) getSupportFragmentManager()
                .findFragmentById(R.id.gpsService);

        mapFragment = (MapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @OnClick(R.id.schoenHierButton)
    void onschoenHierButtonClick() {
        SchoenHierController.getInstance().markCurPosAsSchoenHier(gpsService);
    }

    @OnTouch(R.id.schoenHierButton)
    boolean onschoenHierButtonTouch(MotionEvent arg1) {
        if (arg1.getAction()== MotionEvent.ACTION_DOWN) {
            schoenHierButton.setAlpha((float) 0.8);
        }
        else if (arg1.getAction()==MotionEvent.ACTION_UP){
            schoenHierButton.setAlpha((float) 1);
        }
        return true;
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
                mapsController.drawHeatMapAt(mapPos);
            }
        } else {
            if (tileOverlay != null) {
                tileOverlay.setVisible(false);
            }
        }

        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        preferences.edit().putBoolean("show_heatmap", isHeatmapEnabled).apply();
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
                mapsController.drawLocationsAt(mapPos);
            } else {
                mapsController.setAllLocationsInvisible();
            }
        }

        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        preferences.edit().putBoolean("show_locations", isLocationsEnabled).apply();
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
            gpsService.getCurLocationOnGUIThread(this::initiateMap);
        }
    }

    Marker personMarker;

    private void initiateMap(android.location.Location location) {
        LatLng locationPos = new LatLng(location.getLatitude(), location.getLongitude());
        initiateMapByLatLon(locationPos);
    }

    private void initiateMapByLatLon(LatLng locationPos) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPos, 15));
        googleMap.setMyLocationEnabled(true);
        mapsController.drawHeatMapAt(locationPos);
        mapsController.drawLocationsAt(locationPos);
    }

    private void setPersonPosition(android.location.Location location) {
        personMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    Subscription continuousLocation;

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        isLocationsEnabled = preferences.getBoolean("show_locations", true);
        isHeatmapEnabled = preferences.getBoolean("show_heatmap", true);
        setLocationsEnabled(isLocationsEnabled);
        setHeatmapEnabled(isHeatmapEnabled);
        // ---------- Manual Continuous Location Update ----------
//        continuousLocation = gpsService.getContinuousCurLocation()
//                                            .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::setPersonPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        // ---------- Manual Continuous Location Update ----------
//        continuousLocation.unsubscribe();
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

        Gradient gradient = new Gradient(colors, startPoints);

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