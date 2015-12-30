package com.locator_app.locator.service.locations;


import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.service.Api;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface LocationsApi {

    @POST(Api.version + "/locations")
    Observable<Response<PostLocationResponse>> postLocation(@Body PostLocationRequest request);

    @GET(Api.version + "/locations/{locationId}")
    Observable<Response<LocatorLocation>> locationById(@Path("locationId") String locationId);

    @GET(Api.version + "/locations/nearby")
    Observable<Response<LocationsNearbyResponse>> locationsNearby(@Query("long") double lon,
                                                                  @Query("lat") double lat,
                                                                  @Query("maxDistance") double maxDistance,
                                                                  @Query("limit") int limit);
}
