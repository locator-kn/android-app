package com.locator_app.locator.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.schoenhier.SchoenHiersNearbyResponse;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.util.CacheImageLoader;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.bubble.BubbleView;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private GpsService gpsService;
    private Bitmap currentPos;

    @Bind(R.id.schoenHierButton)
    BubbleView schoenHierButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        String urlCurrentPos = "drawable://" + R.drawable.profile;
        CacheImageLoader.getInstance().loadAsync(urlCurrentPos).subscribe(
                (bitmap -> {
                    currentPos = Bitmap.createScaledBitmap(bitmap, 60, 60, false);
                }),
                (error -> {
                })
        );

        gpsService = GpsService.getInstance();

        schoenHierButton.loadImage("drawable://" + R.drawable.schoenhier);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @OnClick(R.id.schoenHierButton)
    void onschoenHierButtonClick() {
        SchoenHierController.getInstance().markCurPosAsSchoenHier()
                .subscribe(
                        (val) -> {
                            Toast.makeText(getApplicationContext(), "geschoenhiert", Toast.LENGTH_SHORT).show();
                        },
                        (err) -> {
                            Toast.makeText(getApplicationContext(), err.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;

        android.location.Location location = gpsService.getGpsLocation();

        if (location == null) {
            return;
        }

        LatLng locationPos = new LatLng(location.getLatitude(), location.getLongitude());

        BitmapDescriptor currentPosDesc = BitmapDescriptorFactory.fromBitmap(currentPos);
        googleMap.addMarker(new MarkerOptions().position(locationPos)
                .icon(currentPosDesc)
                .anchor((float) 0.5, (float) 0.5));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPos, 15));
        addHeatMap(location.getLongitude(), location.getLatitude());
    }

//    public void drawLocations() {
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location);
//        bitmap = getRoundBitmap(bitmap, 100);
//
//        Location location = gpsService.getGpsLocation();
//        LatLng locationPos = new LatLng(location.getLatitude()  + 0.002,
//                location.getLongitude() + 0.002);
//
//        drawLocation(locationPos, bitmap);
//    }

//    public void drawLocation(LatLng latLong, Bitmap bitmap) {
//        Log.d(LOGTAG, "drawLocation: called");
//        googleMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//                .anchor(0.5f, 0.5f)
//                .position(latLong));
//    }

    private HeatmapTileProvider heatmapTileProvider;
    private TileOverlay tileOverlay;
    private List<LatLng> heatPoints;

    public void addHeatMap(double lon, double lat) {
        heatPoints = new LinkedList<>();

        SchoenHierController.getInstance().schoenHiersNearby(lon, lat, 10, 100)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(response -> response.results)
                .subscribe(
                        (item) -> {
                            double shLon = item.schoenHier.geoTag.getLongitude();
                            double shLat = item.schoenHier.geoTag.getLatitude();

                            heatPoints.add(new LatLng(shLat, shLon));
                        },
                        (error) -> Toast.makeText(getApplicationContext(),
                                "Schön hier nicht bekommen",
                                Toast.LENGTH_SHORT),
                        this::drawHeatMap
                );
    }

    static private int[] colors = {
            Color.rgb(0, 255, 0),
            Color.rgb(255, 160, 0)
    };

    static private float[] startPoints = {
            (float) 0.1,
            (float) 0.8
    };

    private void drawHeatMap() {
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

        tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
    }
}