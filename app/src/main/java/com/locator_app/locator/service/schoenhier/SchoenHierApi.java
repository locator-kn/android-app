package com.locator_app.locator.service.schoenhier;

import com.locator_app.locator.service.Api;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface SchoenHierApi {

    @GET(Api.version + "/schoenhiers/nearby")
    Observable<Response> schoenHiersNearby(@Query("long") double lon,
                                        @Query("lat") double lat,
                                        @Query("maxDistance") double distance,
                                        @Query("limit") int limit);

    @POST(Api.version + "/schoenhiers")
    Observable<Response> markAsSchoenHier(@Body SchoenHierRequest schoenHierRequest);
}
