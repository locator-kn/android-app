package com.locator_app.locator.apiservice.schoenhier;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;

import java.net.UnknownHostException;

import retrofit.HttpException;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;
import rx.functions.Func1;

public class SchoenHierApiService {


    public interface SchoenHierApi {

        @GET(Api.version + "/schoenhiers/nearby")
        Observable<SchoenHiersNearbyResponse> schoenHiersNearby(@Query("long") double lon,
                                               @Query("lat") double lat,
                                               @Query("maxDistance") double distance,
                                               @Query("limit") int limit);

        @POST(Api.version + "/schoenhiers")
        Observable<Response<SchoenHiersResponse>> markAsSchoenHier(@Body SchoenHierRequest schoenHierRequest);
    }

    private SchoenHierApi service = ServiceFactory.createService(SchoenHierApi.class);

    public Observable<SchoenHiersNearbyResponse> schoenHiersNearby(double lon, double lat,
                                                                double distance,
                                                                int limit) {
        return service.schoenHiersNearby(lon, lat, distance, limit);
//                .doOnError(this::handleError)
//                .flatMap(this::parseSchoenHiersNearbyResponse);
    }

    private Observable<SchoenHiersNearbyResponse> parseSchoenHiersNearbyResponse(Response response) {
        if (response.isSuccess()) {
            return Observable.just((SchoenHiersNearbyResponse) response.body());
        }
        return Observable.error(new Exception("http-error: " + Integer.toString(response.code())));
    }

    public Observable<SchoenHiersResponse> markAsSchoenHier(SchoenHierRequest request) {
        return service.markAsSchoenHier(request)
                .onErrorResumeNext(throwable -> {
                    return Observable.error(new Exception(throwable.getMessage()));
                })
                .flatMap(this::parseMarkAsSchoenHierResponse);
    }

    private Observable<SchoenHiersResponse> parseMarkAsSchoenHierResponse(Response<SchoenHiersResponse> response) {
        if (response.isSuccess()) {
            return Observable.just(response.body());
        }
        return Observable.error(new Exception("http-error" + Integer.toString(response.code())));
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException ex = (HttpException) throwable;
        } else if (throwable instanceof UnknownHostException) {
            UnknownHostException ex = (UnknownHostException) throwable;
        }
    }
}
