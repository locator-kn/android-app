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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.CacheImageLoader;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.bubble.BubbleView;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private GpsService gpsService;
    private Bitmap currentPos;
    private Bitmap locationIcon;

    @Bind(R.id.schoenHierButton)
    BubbleView schoenHierButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        String urlCurrentPos = "drawable://" + R.drawable.profile;
        String urlLocation   = "drawable://" + R.drawable.white_location_icon_small;
        CacheImageLoader.getInstance().loadAsync(urlCurrentPos).subscribe(
                (bitmap -> {
                    currentPos = Bitmap.createScaledBitmap(bitmap, 60, 60, false);
                }),
                (error -> {})
        );
        CacheImageLoader.getInstance().loadAsync(urlLocation).subscribe(
                (bitmap -> {
                    locationIcon = Bitmap.createScaledBitmap(bitmap, 60, 60, false);
                }),
                (error -> {})
        );

        gpsService = GpsService.getInstance();

        schoenHierButton.loadImage("drawable://" + R.drawable.schoenhier);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @OnClick(R.id.schoenHierButton)
    void onschoenHierButtonClick() {
        SchoenHierController.getInstance().markCurPosAsSchoenHier();
    }

    private final static int MIN_CALL_DELAY_MS = 2000;
    private long lastCall;
    public boolean calledRecently() {
        long delay = System.currentTimeMillis() - lastCall;
        lastCall = System.currentTimeMillis();
        return delay < MIN_CALL_DELAY_MS;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;

        android.location.Location location = gpsService.getGpsLocation();

        if (location == null) {
            return;
        }
        googleMap.setOnCameraChangeListener( cameraPosition -> {
            if (!calledRecently()) {
//                drawHeatMapAt(cameraPosition.target);
                drawLocationsAt(cameraPosition.target);
            }
        });

        LatLng locationPos = new LatLng(location.getLatitude(), location.getLongitude());

        BitmapDescriptor currentPosDesc = BitmapDescriptorFactory.fromBitmap(currentPos);
        googleMap.addMarker(new MarkerOptions().position(locationPos)
                .icon(currentPosDesc)
                .anchor((float) 0.5, (float) 0.5));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPos, 15));
        addHeatMap(location.getLongitude(), location.getLatitude());
        //drawLocationsAt(locationPos);
    }

    private void drawHeatMapAt(LatLng position) {

    }
    
    HashSet<LocatorLocation> drawnlocations = new HashSet<>();
    Queue<LocatorLocation> newLocations = new LinkedList<>();
    
    private void drawLocationsAt(LatLng position) {
        LocationController.getInstance().getLocationsNearby(position.longitude, position.latitude, 1, 100)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (item) -> newLocations.add(item),
                        (error) -> Toast.makeText(getApplicationContext(),
                                "Fehler beim Laden von Locations",
                                Toast.LENGTH_SHORT),
                        this::drawNewLocations
                );
    }

    public void drawNewLocations() {
        while (!newLocations.isEmpty()) {
            LocatorLocation location = newLocations.poll();
            if (drawnlocations.contains(location)) {
                continue;
            }
            drawLocation(location.geoTag.getLongitude(), location.geoTag.getLatitude());
            drawnlocations.add(location);
        }
    }

    public void drawLocation(double lon, double lat) {
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(locationIcon))
                .anchor(0.5f, 0.5f)
                .position(new LatLng(lat, lon)));
    }

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
        try {
            tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
        } catch(OutOfMemoryError e) {
        }
    }
}