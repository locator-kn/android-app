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
        SchoenHierResponse parsedResponse = (SchoenHierResponse)
                APIUtils.parseResponse(response, SchoenHierResponse.class);
        if (parsedResponse != null) {
            return Observable.just(parsedResponse);
        } else {
            return Observable.error(new SchoenHierParseResponseException());
        }
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException ex = (HttpException) throwable;
        } else if (throwable instanceof UnknownHostException) {
            UnknownHostException ex = (UnknownHostException) throwable;
        }
        throw new NetworkError("schoenHier error");
    }

    public class NetworkError extends RuntimeException {
        public final String errorMessage;

        public NetworkError(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public String toString() {
            return errorMessage;
        }
    }

    public class SchoenHierParseResponseException extends RuntimeException {

    }
}
