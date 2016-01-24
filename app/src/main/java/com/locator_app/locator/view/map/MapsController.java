package com.locator_app.locator.view.map;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.CacheImageLoader;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MapsController {

    private HashSet<LocatorLocation> drawnlocations = new HashSet<>();
    private Queue<LocatorLocation> newLocations = new LinkedList<>();

    private MapsActivity mapsActivity;
    private GoogleMap googleMap;

    private BitmapDescriptor locationIcon;


    public MapsController(MapsActivity maps, GoogleMap map) {
        mapsActivity = maps;
        googleMap = map;

        String urlLocation   = "drawable://" + R.drawable.location_auf_map;
        CacheImageLoader.getInstance().loadAsync(urlLocation).subscribe(
                (bitmap -> {
                    Bitmap icon = Bitmap.createScaledBitmap(bitmap, 70, 70, false);
                    locationIcon = BitmapDescriptorFactory.fromBitmap(icon);
                }),
                (error -> {
                })
        );

        setUpClusterer();
    }


    private ClusterManager<LocationMarker> clusterManager;
    private void setUpClusterer() {
        clusterManager = new ClusterManager<>(LocatorApplication.getAppContext(), googleMap);

        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setRenderer(new LocationMarkerRenderer(LocatorApplication.getAppContext(),
                                                              googleMap,
                                                              clusterManager));
    }

//    private final static int MIN_CALL_DELAY_MS = 2000;
//    private long lastCall;
//    public boolean calledRecently() {
//        long delay = System.currentTimeMillis() - lastCall;
//        lastCall = System.currentTimeMillis();
//        return delay < MIN_CALL_DELAY_MS;
//    }

    public void drawLocationsAt(LatLng position) {
//        if (!calledRecently()) {
//                drawHeatMapAt(cameraPosition.target);
            drawLocationsAtP(position);
//        }
    }

    private void drawLocationsAtP(LatLng position) {
        LocationController.getInstance().getLocationsNearby(position.longitude, position.latitude, 1, 100) //TODO: calculate radius
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        newLocations::add,
                        (error) -> Toast.makeText(LocatorApplication.getAppContext(),
                                "Fehler beim Laden von Locations",
                                Toast.LENGTH_SHORT),
                        this::drawNewLocations
                );
    }

    public void drawNewLocations() {
        boolean drawnNew = false;
        while (!newLocations.isEmpty()) {
            LocatorLocation location = newLocations.poll();
            if (drawnlocations.contains(location)) {
                continue;
            }
            drawnNew = true;
            //drawLocation(location.geoTag.getLongitude(), location.geoTag.getLatitude());
            clusterManager.addItem(new LocationMarker(location.geoTag.getLatitude(),
                                                      location.geoTag.getLongitude(),
                                                      locationIcon));
            drawnlocations.add(location);
        }
        if (drawnNew) {
            clusterManager.cluster();
        }
    }

    private void drawLocation(double lon, double lat) {
//        googleMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromBitmap(locationIcon))
//                .anchor(0.5f, 0.5f)
//                .position(new LatLng(lat, lon)));
    }

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
                        (error) -> Toast.makeText(LocatorApplication.getAppContext(),
                                "Schön hier nicht bekommen",
                                Toast.LENGTH_SHORT),
                        () -> mapsActivity.drawHeatMap(heatPoints)
                );
    }
}
