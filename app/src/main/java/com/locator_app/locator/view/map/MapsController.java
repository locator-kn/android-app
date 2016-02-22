package com.locator_app.locator.view.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.view.LocationDetailActivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MapsController {

    private HashSet<LocatorLocation> drawnlocations = new HashSet<>();
    private Map<LocationMarker, LocatorLocation> markerToLocation = new ConcurrentHashMap<>();
    private Map<String, LocationMarker> markerIDToLocationMarker = new ConcurrentHashMap<>();
    private Queue<LocatorLocation> newLocations = new LinkedList<>();

    private MapsActivity mapsActivity;
    private GoogleMap googleMap;

    private BitmapDescriptor locationIcon;


    public MapsController(MapsActivity maps, GoogleMap map) {
        mapsActivity = maps;
        googleMap = map;

        Glide.with(mapsActivity).load(R.drawable.location_auf_map)
            .asBitmap()
            .override(70, 70)
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                    Bitmap icon = Bitmap.createScaledBitmap(resource, 70, 70, false);
                    locationIcon = BitmapDescriptorFactory.fromBitmap(icon);
                }
            });

        setUpClusterer();
    }


    private ClusterManager<LocationMarker> clusterManager;
    MarkerInfoWindow infoWindow;

    private void setUpClusterer() {
        clusterManager = new ClusterManager<>(LocatorApplication.getAppContext(), googleMap);

        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        googleMap.setOnInfoWindowClickListener(clusterManager);
        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());

        clusterManager.setRenderer(new LocationMarkerRenderer(LocatorApplication.getAppContext(),
                googleMap,
                clusterManager,
                markerIDToLocationMarker));

        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<LocationMarker>() {
            @Override
            public void onClusterItemInfoWindowClick(LocationMarker locationMarker) {
                if (markerToLocation.containsKey(locationMarker)) {
                    LocatorLocation location = markerToLocation.get(locationMarker);

                    Intent intent = new Intent(mapsActivity, LocationDetailActivity.class);
                    intent.putExtra("location", location);
                    mapsActivity.startActivity(intent);
                }
            }
        });

        infoWindow = new MarkerInfoWindow();
        infoWindow.onCreateView(mapsActivity.getLayoutInflater(), null, null);

        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (markerIDToLocationMarker.containsKey(marker.getId())) {
                    LocationMarker locationMarker = markerIDToLocationMarker.get(marker.getId());
                    if (markerToLocation.containsKey(locationMarker)) {
                        LocatorLocation location = markerToLocation.get(locationMarker);

                        infoWindow.setImage(location.images.getNormal(), mapsActivity);
                        infoWindow.setLocationTitle(location.title);

                        return infoWindow.getView();
                    }
                }
                return null;
            }
        });
    }

//    private final static int MIN_CALL_DELAY_MS = 2000;
//    private long lastCall;
//    public boolean calledRecently() {
//        long delay = System.currentTimeMillis() - lastCall;
//        lastCall = System.currentTimeMillis();
//        return delay < MIN_CALL_DELAY_MS;
//    }

    public void drawLocationsAt(LatLng position) {
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
            LocationMarker marker = new LocationMarker(location.geoTag.getLatitude(),
                                                       location.geoTag.getLongitude(),
                                                       locationIcon);
            clusterManager.addItem(marker);
            markerToLocation.put(marker, location);
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
