package com.locator_app.locator.apiservice.locations;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.Impression;

import java.util.List;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public class LocationsApiService {

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

        @GET(Api.version + "/locations/users/{userId}")
        Observable<Response<List<LocatorLocation>>> getLocationsByUser(@Path("userId") String userId);

        @GET(Api.version + "/locations/{locationId}/impressions")
        Observable<Response<List<Impression>>> getImpressionsByLocationId(@Path("locationId") String locationId);

        @POST(Api.version + "/locations/{locationId}/favor")
        Observable<Response<UnFavorResponse>> favorLocation(@Path("locationId") String locationId);

        @POST(Api.version + "/locations/{locationId}/unfavor")
        Observable<Response<UnFavorResponse>> unfavorLocation(@Path("locationId") String locationId);
    }

    LocationsApi service = ServiceFactory.createService(LocationsApi.class);

    public Observable<LocatorLocation> getLocationById(String locationId) {
        return GenericErrorHandler.wrapSingle(service.locationById(locationId));
    }

    public Observable<LocationsNearbyResponse> getLocationsNearby(double lon, double lat, double maxRadius, int limit) {
        return GenericErrorHandler.wrapSingle(service.locationsNearby(lon, lat, maxRadius, limit));
    }

    public Observable<LocatorLocation> getLocationsByUser(String userId) {
        return GenericErrorHandler.wrapList(service.getLocationsByUser(userId));
    }

    public Observable<AbstractImpression> getImpressionsByLocationId(String locationId) {
        return GenericErrorHandler.wrapList(service.getImpressionsByLocationId(locationId))
                .map(AbstractImpression::createImpression);
    }

    public Observable<UnFavorResponse> favorLocation(String locationId) {
        return GenericErrorHandler.wrapSingle(service.favorLocation(locationId));
    }

    public Observable<UnFavorResponse> unfavorLocation(String locationId) {
        return GenericErrorHandler.wrapSingle(service.unfavorLocation(locationId));
    }
}
