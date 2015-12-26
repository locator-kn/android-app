package com.locator_app.locator.service;


import com.squareup.okhttp.Response;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface SchoenHierApi {

    @GET("schoenhiers/nearby")
    Observable<Response> getSchoenHiers(@Query("long") double lon,
                                        @Query("lat") double lat,
                                        @Query("maxDistance") double distance,
                                        @Query("limit") int limit);
}
