package com.locator_app.locator.controller;


import android.graphics.Bitmap;

import com.locator_app.locator.apiservice.locations.UnFavorResponse;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.apiservice.locations.LocationsApiService;
import com.locator_app.locator.model.impressions.AbstractImpression;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LocationController {

    LocationsApiService locationService;

    public Observable<LocatorLocation> getLocationById(String locationId) {
        return locationService.getLocationById(locationId);
    }


    public Observable<LocatorLocation> getLocationsNearby(double lon, double lat, double maxDistance,
                                                          int limit) {
        return locationService.getLocationsNearby(lon, lat, maxDistance, limit)
                .map(locationsNearbyResponse -> locationsNearbyResponse.results)
                .flatMapIterable(list -> list)
                .map(x -> x.location);
    }

    public Observable<LocatorLocation> getLocationsByUserId(String userId) {
        return locationService.getLocationsByUser(userId);
    }

    public Observable<AbstractImpression> getImpressionsByLocationId(String locationId) {
        return locationService.getImpressionsByLocationId(locationId);
    }

    public Observable<UnFavorResponse> favorLocation(String locationId) {
        return locationService.favorLocation(locationId);
    }

    public Observable<UnFavorResponse> unfavorLocation(String locationId) {
        return locationService.unfavorLocation(locationId);
    }

    public Observable<LocationsApiService.EchoResponse> createImageImpression(String locationId, Bitmap image) {
        return locationService.createImageImpression(locationId, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static LocationController instance;
    public static LocationController getInstance() {
        if (instance == null) {
            instance = new LocationController();
        }
        return instance;
    }
    private LocationController() {
        locationService = new LocationsApiService();
    }
}
