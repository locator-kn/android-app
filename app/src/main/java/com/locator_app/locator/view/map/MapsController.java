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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.schoenhier.SchoenHiersResponse;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.model.Categories;
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

import rx.Observable;

public class MapsController {

    private HashSet<LocatorLocation> drawnlocations = new HashSet<>();
    private Map<Marker, LocatorLocation> markerIdToLocation = new ConcurrentHashMap<>();
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

        setupInfoWindow();
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

    MarkerInfoWindow infoWindow;
    private void setupInfoWindow() {

        infoWindow = new MarkerInfoWindow();
        infoWindow.onCreateView(mapsActivity.getLayoutInflater(), null, null);

        googleMap.setOnInfoWindowClickListener(marker -> {
            LocatorLocation location = markerIdToLocation.get(marker);

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
        });

        googleMap.setInfoWindowAdapter(
                new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoWindow(Marker marker) {
                        LocatorLocation location = markerIdToLocation.get(marker);
                        infoWindow.setFollowers(location.favorites.size());
                        infoWindow.setLocationTitle(location.title);
                        infoWindow.setImage(location.images.getNormal(), mapsActivity, marker);
                        infoWindow.setCreatorName(location.userId, marker);
                        return infoWindow.getView();
                    }
                });
    }

    public void setAllLocationsInvisible() {
        for (Marker marker : markerIdToLocation.keySet()) {
            marker.setVisible(false);
        }
    }

    public void setAllLocationsVisible() {
        for (Marker marker : markerIdToLocation.keySet()) {
            marker.setVisible(true);
        }
    }

    public void redrawLocationsAt(LatLng position) {
        drawLocationsAt(position).subscribe(
                (res) -> {},
                (err) -> {}
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

    public void drawNewLocations() {
        while (!newLocations.isEmpty()) {
            LocatorLocation location = newLocations.poll();
            if (drawnlocations.contains(location)) {
                continue;
            }
            if (location.images.hasEgg()) {
                Glide.with(mapsActivity).load(location.images.getEgg())
                        .asBitmap()
                        .error(getLocationMapThumbnailIcon(location))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap icon, GlideAnimation glideAnimation) {
                                Bitmap resized = Bitmap.createScaledBitmap(icon, thumbnailSize, thumbnailSize, false);
                                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(resized);
                                drawMarker(descriptor, location);
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
    }

    private void drawMarker(BitmapDescriptor icon, LocatorLocation location) {
        LatLng latLng = new LatLng(location.geoTag.getLatitude(), location.geoTag.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(icon);
        Marker marker = googleMap.addMarker(markerOptions);
        markerIdToLocation.put(marker, location);
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
