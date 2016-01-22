package com.locator_app.locator.controller;

import android.widget.Toast;

import com.locator_app.locator.LocatorApplication;
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

    public void markCurPosAsSchoenHier(GpsService gpsService) {
        gpsService.getCurLocationOnIOThread(this::markAsSchoenHier);
    }

    private void markAsSchoenHier(android.location.Location location) {
        SchoenHierRequest request = new SchoenHierRequest();
        request.lon = location.getLongitude();
        request.lat = location.getLatitude();
        markAsSchoenHier(request);
    }

    public Observable<SchoenHiersResponse> markAsSchoenHier(SchoenHierRequest request) {
        return schoenHierService.markAsSchoenHier(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError((exception) -> Toast.makeText(LocatorApplication.getAppContext(),
                                                         "Could not mark as Schön Hier",
                                                         Toast.LENGTH_LONG).show())
                .doOnCompleted(() -> Toast.makeText(LocatorApplication.getAppContext(),
                                                    "geschönhiert",
                                                    Toast.LENGTH_SHORT).show());
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
