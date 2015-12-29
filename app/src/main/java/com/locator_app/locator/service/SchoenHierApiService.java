package com.locator_app.locator.service;

import java.net.UnknownHostException;

import retrofit.HttpException;
import retrofit.Response;
import rx.Observable;

public class SchoenHierApiService {

    private SchoenHierApi service = ServiceFactory.createService(SchoenHierApi.class);

    public Observable<SchoenHierResponse> getSchoenHierResponse(double lon, double lat,
                                                                double distance,
                                                                int limit) {
        return service.getSchoenHiers(lon, lat, distance, limit)
                .doOnError(this::handleError)
                .flatMap(this::parseSchoenHierResponse);
    }

    private Observable<SchoenHierResponse> parseSchoenHierResponse(Response response) {
        if (response.isSuccess()) {
            return Observable.just((SchoenHierResponse) response.body());
        }
        return Observable.error(new Exception("http-error: " + Integer.toString(response.code())));
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException ex = (HttpException) throwable;
        } else if (throwable instanceof UnknownHostException) {
            UnknownHostException ex = (UnknownHostException) throwable;
        }
    }
}
