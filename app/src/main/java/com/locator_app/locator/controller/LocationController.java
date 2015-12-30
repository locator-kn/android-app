package com.locator_app.locator.controller;


import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.service.locations.LocationsApiService;
import com.locator_app.locator.service.locations.LocationsNearbyResponse;
import com.locator_app.locator.service.schoenhier.SchoenHierApiService;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

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
