package com.locator_app.locator.service;

import rx.Observable;

public class SchoenHierRequestManager {

    SchoenHierApiService service;

    public Observable<SchoenHierResponse> getSchoenHiers() {
        if (service == null) {
            service = new SchoenHierApiService();
        }
        double lon = 9.169753789901733;
        double lat = 47.66868204997508;
        double dis = 2;
        int max = 3;
        return service.getSchoenHierResponse(lon, lat, dis, max);
    }
}
