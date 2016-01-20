package com.locator_app.locator.controller;

import com.locator_app.locator.apiservice.schoenhier.SchoenHierApiService;
import com.locator_app.locator.apiservice.schoenhier.SchoenHierRequest;
import com.locator_app.locator.apiservice.schoenhier.SchoenHiersNearbyResponse;
import com.locator_app.locator.apiservice.schoenhier.SchoenHiersResponse;
import com.locator_app.locator.util.GpsService;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SchoenHierController {

    SchoenHierApiService schoenHierService;

    public Observable<SchoenHiersNearbyResponse> schoenHiersNearby(double lon, double lat,
                                                                   double dist, int max) {
        return schoenHierService.schoenHiersNearby(lon, lat, dist, max);
    }

    public Observable<SchoenHiersResponse> markCurPosAsSchoenHier() {
        GpsService gpsService = GpsService.getInstance();
        if (!gpsService.isGpsEnabled()) {
            return Observable.error(new Exception("Gps is not enabled"));
        }

        android.location.Location location = gpsService.getGpsLocation();
        if (location == null) {
            return Observable.error(new Exception("could not determine your current gps position"));
        }

        SchoenHierRequest request = new SchoenHierRequest();
        request.lon = location.getLongitude();
        request.lat = location.getLatitude();
        return markAsSchoenHier(request);
    }

    public Observable<SchoenHiersResponse> markAsSchoenHier(SchoenHierRequest request) {
        return schoenHierService.markAsSchoenHier(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static SchoenHierController instance;
    public static SchoenHierController getInstance() {
        if (instance == null) {
            instance = new SchoenHierController();
        }
        return instance;
    }
    private SchoenHierController() {
        schoenHierService = new SchoenHierApiService();
    }
}
