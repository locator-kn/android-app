package com.locator_app.locator.service;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface SchoenHierService {

    @GET("schoenhiers/nearby")
    Observable<SchoenHierResponse> getSchoenHiers(@Query("long") double lon,
                                                  @Query("lat") double lat,
                                                  @Query("maxDistance") double distance,
                                                  @Query("limit") int limit);
}
