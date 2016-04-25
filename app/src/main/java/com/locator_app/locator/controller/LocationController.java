package com.locator_app.locator.controller;


import android.graphics.Bitmap;

import com.locator_app.locator.apiservice.locations.LocationsApiService;
import com.locator_app.locator.apiservice.locations.UnFavorResponse;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.util.AppTracker;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LocationController {

    LocationsApiService locationService;

    public Observable<LocatorLocation> getLocationById(String locationId) {
        return locationService.getLocationById(locationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<LocatorLocation> getLocationsNearby(double lon, double lat, double maxDistance,
                                                          int limit) {
        return locationService.getLocationsNearby(lon, lat, maxDistance, limit)
                .flatMapIterable(list -> list)
                .map(x -> x.location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<LocatorLocation> getLocationsByUserId(String userId) {
        return locationService.getLocationsByUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<LocatorLocation> getFavoritedLocations(String userId) {
        return locationService.getFavoritedLocations(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AbstractImpression> getImpressionsByLocationId(String locationId) {
        return locationService.getImpressionsByLocationId(locationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UnFavorResponse> favorLocation(String locationId) {
        return locationService.favorLocation(locationId)
                .doOnNext((val) -> AppTracker.getInstance().track("Locationview | like"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UnFavorResponse> unfavorLocation(String locationId) {
        return locationService.unfavorLocation(locationId)
                .doOnNext((val) -> AppTracker.getInstance().track("Locationview | unlike"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Object> createImageImpression(String locationId, Bitmap image) {
        return locationService.createImageImpression(locationId, image)
                .doOnNext((val) -> AppTracker.getInstance().track("Locationview | imageimpression success"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Object> createVideoImpression(String locationId, byte[] videoData) {
        return locationService.createVideoImpression(locationId, videoData)
                .doOnNext((val) -> AppTracker.getInstance().track("Locationview | videoimpression success"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<LocatorLocation> createLocation(String title,
                                             double  lon,
                                             double  lat,
                                             String[]  categories,
                                             Bitmap bitmap) {
        return locationService.createLocation(title, lon, lat, categories, bitmap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Object> createTextImpression(String locationId, String text) {
        return locationService.createTextImpression(locationId, text)
                .doOnNext((val) -> AppTracker.getInstance().track("Locationview | textimpression success"))
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
