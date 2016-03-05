package com.locator_app.locator.apiservice.schoenhier;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;

import java.net.UnknownHostException;
import java.util.List;

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
        Observable<Response<List<SchoenHiersResponse>>> schoenHiersNearby(@Query("long") double lon,
                                               @Query("lat") double lat,
                                               @Query("maxDistance") double distance,
                                               @Query("limit") int limit);

        @POST(Api.version + "/schoenhiers")
        Observable<Response<SchoenHiersResponse>> markAsSchoenHier(@Body SchoenHierRequest schoenHierRequest);
    }

    private SchoenHierApi service = ServiceFactory.createService(SchoenHierApi.class);

    public Observable<SchoenHiersResponse> schoenHiersNearby(double lon, double lat,
                                                                double distance,
                                                                int limit) {
        return GenericErrorHandler.wrapList(service.schoenHiersNearby(lon, lat, distance, limit));
    }

    public Observable<SchoenHiersResponse> markAsSchoenHier(SchoenHierRequest request) {
        return GenericErrorHandler.wrapSingle(service.markAsSchoenHier(request));
    }

}
