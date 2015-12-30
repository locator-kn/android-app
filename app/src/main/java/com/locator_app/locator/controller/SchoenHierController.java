package com.locator_app.locator.controller;

import com.locator_app.locator.service.schoenhier.SchoenHierApiService;
import com.locator_app.locator.service.schoenhier.SchoenHierRequest;
import com.locator_app.locator.service.schoenhier.SchoenHiersNearbyResponse;
import com.locator_app.locator.service.schoenhier.SchoenHiersResponse;

import rx.Observable;

public class SchoenHierController {

    SchoenHierApiService schoenHierService;

    public Observable<SchoenHiersNearbyResponse> schoenHiersNearby(double lon, double lat,
                                                                   double dist, int max) {
        return schoenHierService.schoenHiersNearby(lon, lat, dist, max);
    }

    public Observable<SchoenHiersResponse> markAsSchoenHier(SchoenHierRequest request) {
        return schoenHierService.markAsSchoenHier(request);
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
