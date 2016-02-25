package com.locator_app.locator.view.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.ClusterManager;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.DistanceCalculator;
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

                    LocationController.getInstance().getLocationById(location.id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    (newLocationFromServer) -> {
                                        Intent intent = new Intent(mapsActivity, LocationDetailActivity.class);
                                        intent.putExtra("location", newLocationFromServer);
                                        mapsActivity.startActivity(intent);
                                    },
                                    (error) -> {
                                        Intent intent = new Intent(mapsActivity, LocationDetailActivity.class);
                                        intent.putExtra("location", location);
                                        mapsActivity.startActivity(intent);
                                    }
                            );
                }
            }
        });

        infoWindow = new MarkerInfoWindow();
        infoWindow.onCreateView(mapsActivity.getLayoutInflater(), null, null);

        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                if (markerIDToLocationMarker.containsKey(marker.getId())) {
                    LocationMarker locationMarker = markerIDToLocationMarker.get(marker.getId());
                    if (markerToLocation.containsKey(locationMarker)) {
                        LocatorLocation location = markerToLocation.get(locationMarker);

                        infoWindow.setFollowers(location.favorites.size());
                        infoWindow.setLocationTitle(location.title);
                        infoWindow.setImage(location.images.getNormal(), mapsActivity, marker);
                        infoWindow.setCreatorName(location.userId, marker);

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

    public void setAllLocationsInvisible() {
        googleMap.setOnCameraChangeListener(null);
        googleMap.setOnMarkerClickListener(null);
        googleMap.setOnInfoWindowClickListener(null);
        googleMap.setInfoWindowAdapter(null);
        for (Marker marker : clusterManager.getClusterMarkerCollection().getMarkers()) {
            marker.setVisible(false);
        }
        for (Marker marker : clusterManager.getMarkerCollection().getMarkers()) {
            marker.setVisible(false);
        }
    }

    public void setAllLocationsVisible() {
        for (Marker marker : clusterManager.getClusterMarkerCollection().getMarkers()) {
            marker.setVisible(true);
        }
        for (Marker marker : clusterManager.getMarkerCollection().getMarkers()) {
            marker.setVisible(true);
        }
        clusterManager.onCameraChange(googleMap.getCameraPosition());
        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        googleMap.setOnInfoWindowClickListener(clusterManager);
        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
    }

    public void drawLocationsAt(LatLng position) {
        if (!mapsActivity.isLocationsEnabled()) {
            return;
        }

        LatLngBounds newLoadableRect = getNewLoadableRect(locationsLoadedRect);
        if (newLoadableRect == null) {
            return;
        }
        locationsLoadedRect = newLoadableRect;

        LocationController.getInstance().getLocationsNearby(position.longitude, position.latitude,
                                                            loadableRadius(locationsLoadedRect), 100)
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

    private List<LatLng> heatPoints = new LinkedList<>();

    public void addHeatPoint(LatLng latLng) {
        heatPoints.add(latLng);
    }

    public void redrawHeatpoints() {
        mapsActivity.drawHeatMap(heatPoints);
    }

    public void addHeatpointAndRedraw(LatLng latLng) {
        addHeatPoint(latLng);
        redrawHeatpoints();
    }

    public void drawHeatMapAt(LatLng pos) {
        if (!mapsActivity.isHeatmapEnabled()) {
            return;
        }
        LatLngBounds newLoadableRect = getNewLoadableRect(heatmapLoadedRect);
        if (newLoadableRect == null) {
            return;
        }
        heatmapLoadedRect = newLoadableRect;

        SchoenHierController.getInstance().schoenHiersNearby(pos.longitude, pos.latitude,
                                                             loadableRadius(heatmapLoadedRect), 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(response -> response.results)
                .subscribe(
                        (item) -> {
                            double shLon = item.schoenHier.geoTag.getLongitude();
                            double shLat = item.schoenHier.geoTag.getLatitude();
                            LatLng shPoint = new LatLng(shLat, shLon);

                            if (!heatPoints.contains(shPoint)) {
                                heatPoints.add(shPoint);
                            }
                        },
                        (error) -> Toast.makeText(LocatorApplication.getAppContext(),
                                "Schön hier nicht bekommen",
                                Toast.LENGTH_SHORT),
                        () -> mapsActivity.drawHeatMap(heatPoints)
                );
    }

    private LatLngBounds locationsLoadedRect = null;
    private LatLngBounds heatmapLoadedRect = null;

    private LatLngBounds getNewLoadableRect(LatLngBounds loadedRect) {
        VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();
        LatLng southwest = visibleRegion.latLngBounds.southwest;
        LatLng northeast = visibleRegion.latLngBounds.northeast;

        if (loadedRect == null) {
            return new LatLngBounds(southwest, northeast);
        } else {
            if (loadedRect.contains(visibleRegion.farLeft)  &&
                loadedRect.contains(visibleRegion.farRight) &&
                loadedRect.contains(visibleRegion.nearLeft) &&
                loadedRect.contains(visibleRegion.nearRight)) {
                return null;
            }
            return LatLngBounds.builder().include(loadedRect.southwest)
                    .include(loadedRect.northeast)
                    .include(visibleRegion.farLeft)
                    .include(visibleRegion.farRight)
                    .include(visibleRegion.nearLeft)
                    .include(visibleRegion.nearRight)
                    .build();
        }
    }

    private int loadableRadius(LatLngBounds loadableRect) {
        LatLng southwest = loadableRect.southwest;
        LatLng northeast = loadableRect.northeast;
        return (int) Math.ceil(DistanceCalculator.distanceInKm(southwest.latitude,
                                                               southwest.longitude,
                                                               northeast.latitude,
                                                               northeast.longitude));
    }
}
