package com.locator_app.locator.controller;

import android.widget.Toast;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.schoenhier.SchoenHierApiService;
import com.locator_app.locator.apiservice.schoenhier.SchoenHierRequest;
import com.locator_app.locator.apiservice.schoenhier.SchoenHiersNearbyResponse;
import com.locator_app.locator.apiservice.schoenhier.SchoenHiersResponse;
import com.locator_app.locator.util.GpsService;

import rx.Observable;

public class SchoenHierController {

    SchoenHierApiService schoenHierService;

    public Observable<SchoenHiersNearbyResponse> schoenHiersNearby(double lon, double lat,
                                                                   double dist, int max) {
        return schoenHierService.schoenHiersNearby(lon, lat, dist, max);
    }

    public void markCurPosAsSchoenHier() {
        GpsService gpsService = GpsService.getInstance();
        if (!gpsService.isGpsEnabled()) {
            Toast.makeText(LocatorApplication.getAppContext(), "Gps is not Enabled", Toast.LENGTH_LONG).show();
            return;
        }

        android.location.Location location = gpsService.getGpsLocation();
        if (location == null) {
            return;
        }

        SchoenHierRequest request = new SchoenHierRequest();
        request.lon = location.getLongitude();
        request.lat = location.getLatitude();

        SchoenHierController.getInstance().markAsSchoenHier(request);
        Toast.makeText(LocatorApplication.getAppContext(), "geschönhiert", Toast.LENGTH_LONG).show();
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
