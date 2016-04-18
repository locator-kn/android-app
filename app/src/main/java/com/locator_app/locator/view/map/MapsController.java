package com.locator_app.locator.view.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

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
import com.locator_app.locator.apiservice.schoenhier.SchoenHiersResponse;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.model.Categories;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.DistanceCalculator;
import com.locator_app.locator.view.LocationDetailActivity;
import com.locator_app.locator.view.UiError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;

public class MapsController {

    private HashSet<LocatorLocation> drawnlocations = new HashSet<>();
    private Map<LocationMarker, LocatorLocation> markerToLocation = new ConcurrentHashMap<>();
    private Map<String, LocationMarker> markerIDToLocationMarker = new ConcurrentHashMap<>();
    private Queue<LocatorLocation> newLocations = new LinkedList<>();

    private MapsActivity mapsActivity;
    private GoogleMap googleMap;


    private Map<String, BitmapDescriptor> categoryIcons = new HashMap<>();
    private BitmapDescriptor locationIcon;
    final int thumbnailSize = 80;

    public MapsController(MapsActivity maps, GoogleMap map) {
        mapsActivity = maps;
        googleMap = map;

        loadCategoryIcons();
        final Bitmap bitmap = BitmapFactory.decodeResource(mapsActivity.getResources(), R.drawable.location_auf_map);
        final Bitmap resized = Bitmap.createScaledBitmap(bitmap, thumbnailSize, thumbnailSize, false);
        locationIcon = BitmapDescriptorFactory.fromBitmap(resized);

        setUpClusterer();
    }

    private void loadCategoryIcons() {
        loadCategoryIcon(Categories.CULTURE);
        loadCategoryIcon(Categories.GASTRO);
        loadCategoryIcon(Categories.HOLIDAY);
        loadCategoryIcon(Categories.NATURE);
        loadCategoryIcon(Categories.NIGHTLIFE);
        loadCategoryIcon(Categories.SECRET);
    }

    private void loadCategoryIcon(String category) {
        final int id = Categories.getLightCategoryIcon(category);
        final Bitmap bitmap = BitmapFactory.decodeResource(mapsActivity.getResources(), id);
        final Bitmap resized = Bitmap.createScaledBitmap(bitmap, thumbnailSize, thumbnailSize, false);
        final BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(resized);
        categoryIcons.put(category, descriptor);
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

        clusterManager.setOnClusterItemInfoWindowClickListener(locationMarker -> {
            if (markerToLocation.containsKey(locationMarker)) {
                LocatorLocation location = markerToLocation.get(locationMarker);

                LocationController.getInstance().getLocationById(location.id)
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

    public void redrawLocationsAt(LatLng position) {
        drawLocationsAt(position).subscribe(
                (res) -> {
                },
                (err) -> {
                }
        );
    }

    public Observable<LocatorLocation> drawLocationsAt(LatLng position) {
        if (!mapsActivity.isLocationsEnabled()) {
            return Observable.error(new Exception());
        }

        LatLngBounds newLoadableRect = getNewLoadableRect(locationsLoadedRect);
        if (newLoadableRect == null) {
            return Observable.error(new Exception());
        }
        locationsLoadedRect = newLoadableRect;

        return LocationController.getInstance().getLocationsNearby(position.longitude, position.latitude,
                loadableRadius(locationsLoadedRect), 100)
                .doOnNext(newLocations::add)
                .doOnCompleted(this::drawNewLocations);
    }

    private int getLocationMapThumbnailIcon(LocatorLocation location) {
        if (location.categories.isEmpty()) {
            return R.drawable.location_auf_map;
        }
        String category = location.categories.get(0);
        int id = Categories.getLightCategoryIcon(category);
        if (id == Categories.NO_ICON) {
            return R.drawable.location_auf_map;
        }
        return id;
    }

    boolean alreadyDrawn;
    public void drawNewLocations() {

        final int width = 80;

        boolean drawnNew = false;
        alreadyDrawn = false;
        while (!newLocations.isEmpty()) {
            LocatorLocation location = newLocations.poll();
            if (drawnlocations.contains(location)) {
                continue;
            }
            drawnNew = true;
            if (!location.images.hasEgg()) {
                Glide.with(mapsActivity).load(location.images.getEgg())
                        .asBitmap()
                        .error(getLocationMapThumbnailIcon(location))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap icon, GlideAnimation glideAnimation) {
                                Bitmap resized = Bitmap.createScaledBitmap(icon, width, width, false);
                                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(resized);
                                drawMarker(descriptor, location);
                                if (alreadyDrawn) {
                                    clusterManager.cluster();
                                }
                            }
                        });
            }
            else {
                BitmapDescriptor descriptor = locationIcon;
                if (!location.categories.isEmpty()) {
                    final String category = location.categories.get(0);
                    descriptor = categoryIcons.get(category);
                }
                drawMarker(descriptor, location);
            }
            drawnlocations.add(location);
        }
        if (drawnNew) {
            alreadyDrawn = true;
            clusterManager.cluster();
        }
    }

    private void drawMarker(BitmapDescriptor icon, LocatorLocation location) {
        LocationMarker marker = new LocationMarker(location.geoTag.getLatitude(),
                location.geoTag.getLongitude(),
                icon);
        clusterManager.addItem(marker);
        markerToLocation.put(marker, location);
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

    public void redrawHeatMapAt(LatLng pos) {
        drawHeatMapAt(pos).subscribe(
                (res) -> {
                },
                (err) -> {
                }
        );
    }

    public Observable<SchoenHiersResponse> drawHeatMapAt(LatLng pos) {
        if (!mapsActivity.isHeatmapEnabled()) {
            return Observable.error(new Exception());
        }
        LatLngBounds newLoadableRect = getNewLoadableRect(heatmapLoadedRect);
        if (newLoadableRect == null) {
            return Observable.error(new Exception());
        }
        heatmapLoadedRect = newLoadableRect;

        return SchoenHierController.getInstance().schoenHiersNearby(pos.longitude, pos.latitude,
                loadableRadius(heatmapLoadedRect), 1000)
                .doOnError((error) -> UiError.showError(mapsActivity,
                        error,
                        "Schön hier nicht bekommen"))
                .doOnNext((item) -> {
                    double shLon = item.schoenHier.geoTag.getLongitude();
                    double shLat = item.schoenHier.geoTag.getLatitude();
                    LatLng shPoint = new LatLng(shLat, shLon);

                    if (!heatPoints.contains(shPoint)) {
                        heatPoints.add(shPoint);
                    }
                })
                .doOnCompleted(() -> mapsActivity.drawHeatMap(heatPoints));
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
