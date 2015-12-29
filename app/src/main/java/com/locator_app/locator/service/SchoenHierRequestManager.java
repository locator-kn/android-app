package com.locator_app.locator.service;

import rx.Observable;

public class SchoenHierRequestManager {

    SchoenHierApiService service = new SchoenHierApiService();

    public Observable<SchoenHiersNearbyResponse> schoenHiersNearby(double lon, double lat,
                                                                   double dist, int max) {
        return service.schoenHiersNearby(lon, lat, dist, max);
    }

    public Observable<SchoenHiersResponse> markAsSchoenHier(SchoenHierRequest request) {
        return service.markAsSchoenHier(request);
    }
}
