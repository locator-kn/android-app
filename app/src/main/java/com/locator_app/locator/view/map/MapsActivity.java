package com.locator_app.locator.view.map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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
import com.locator_app.locator.R;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.bubble.BubbleView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Bitmap currentPos;
    private MapsController mapsController;

    @Bind(R.id.schoenHierButton)
    BubbleView schoenHierButton;

    GpsService gpsService;

    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);


        Glide.with(this).load(R.drawable.profile).asBitmap().override(60, 60).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                MapsActivity.this.currentPos = resource;
            }
        });

        schoenHierButton.setImage(R.drawable.schoenhier);

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

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mapsController = new MapsController(this, googleMap);
        mapFragment.init(mapsController, googleMap);

        gpsService.getCurLocationOnGUIThread(this::initiateMap);
    }

    Marker personMarker;

    private void initiateMap(android.location.Location location) {
        LatLng locationPos = new LatLng(location.getLatitude(), location.getLongitude());

        // ---------- Manual Continuous Location Update ----------
//        BitmapDescriptor currentPosDesc = BitmapDescriptorFactory.fromBitmap(currentPos);
//        personMarker = googleMap.addMarker(new MarkerOptions().position(locationPos)
//                .icon(currentPosDesc)
//                .anchor((float) 0.5, (float) 0.5));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPos, 15));
        googleMap.setMyLocationEnabled(true);
        //googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mapsController.addHeatMap(location.getLongitude(), location.getLatitude());
        mapsController.drawLocationsAt(locationPos);
    }

    private void setPersonPosition(android.location.Location location) {
        personMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    Subscription continuousLocation;

    @Override
    public void onResume() {
        super.onResume();
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

    private HeatmapTileProvider heatmapTileProvider;
    private TileOverlay tileOverlay;

    static private int[] colors = {
            Color.rgb(0, 255, 0),
            Color.rgb(255, 160, 0)
    };

    static private float[] startPoints = {
            (float) 0.1,
            (float) 0.8
    };

    public void drawHeatMap(List<LatLng> heatPoints) {
        if (heatPoints.isEmpty()) {
            return;
        }

        Gradient gradient = new Gradient(colors, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        heatmapTileProvider = new HeatmapTileProvider.Builder()
                .data(heatPoints)
                .radius(50)
//                .opacity(0.6)
//                .gradient(gradient)
                .build();
        try {
            tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
        } catch(OutOfMemoryError e) {
        }
    }
}