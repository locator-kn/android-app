package com.locator_app.locator.controller;


import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.apiservice.locations.LocationsApiService;

import rx.Observable;

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
