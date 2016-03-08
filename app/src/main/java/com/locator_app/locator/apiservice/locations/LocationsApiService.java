package com.locator_app.locator.apiservice.locations;

import android.graphics.Bitmap;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.Impression;
import com.locator_app.locator.util.BitmapHelper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.List;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public class LocationsApiService {

    public interface LocationsApi {

        @GET(Api.version + "/locations/{locationId}")
        Observable<Response<LocatorLocation>> locationById(@Path("locationId") String locationId);

        @GET(Api.version + "/locations/nearby")
        Observable<Response<List<LocationResponse>>> locationsNearby(@Query("long") double lon,
                                                                      @Query("lat") double lat,
                                                                      @Query("maxDistance") double maxDistance,
                                                                      @Query("limit") int limit);

        @GET(Api.version + "/locations/users/{userId}")
        Observable<Response<List<LocatorLocation>>> getLocationsByUser(@Path("userId") String userId);

        @GET(Api.version + "/locations/users/{userId}/favored")
        Observable<Response<List<LocatorLocation>>> getFavoritedLocations(@Path("userId") String userId);

        @GET(Api.version + "/locations/{locationId}/impressions")
        Observable<Response<List<Impression>>> getImpressionsByLocationId(@Path("locationId") String locationId);

        @POST(Api.version + "/locations/{locationId}/favor")
        Observable<Response<UnFavorResponse>> favorLocation(@Path("locationId") String locationId);

        @POST(Api.version + "/locations/{locationId}/unfavor")
        Observable<Response<UnFavorResponse>> unfavorLocation(@Path("locationId") String locationId);

        @Multipart
        @POST(Api.version + "/locations/{locationId}/impressions/image")
        Observable<Response<Object>> postImageImpression(@Path("locationId") String locationId,
                                                         @Part("file\"; filename=impression.jpg") RequestBody file);

        @Multipart
        @POST(Api.version + "/locations")
        Observable<Response<LocatorLocation>> createLocation(@Part("title") RequestBody  title,
                                                    @Part("long") RequestBody  lon,
                                                    @Part("lat") RequestBody  lat,
                                                    @Part("categories") String[]  categories,
                                                    @Part("file\"; filename=image.jpg") RequestBody file);

        @Multipart
        @POST(Api.version + "/locations/{locationId}/impressions/video")
        Observable<Response<Object>> postVideoImpression(@Path("locationId") String locationId,
             @Part("file\"; filename=impression.3gp") RequestBody file);

        @POST(Api.version + "/locations/{locationId}/impressions/text")
        Observable<Response<Object>> postTextImpression(@Path("locationId") String locationId,
                                                        @Body TextImpressionRequest text);
    }

    LocationsApi service = ServiceFactory.createService(LocationsApi.class);

    public Observable<LocatorLocation> getLocationById(String locationId) {
        return GenericErrorHandler.wrapSingle(service.locationById(locationId));
    }

    public Observable<List<LocationResponse>> getLocationsNearby(double lon, double lat, double maxRadius, int limit) {
        return GenericErrorHandler.wrapSingle(service.locationsNearby(lon, lat, maxRadius, limit));
    }

    public Observable<LocatorLocation> getLocationsByUser(String userId) {
        return GenericErrorHandler.wrapList(service.getLocationsByUser(userId));
    }

    public Observable<LocatorLocation> getFavoritedLocations(String userId) {
        return GenericErrorHandler.wrapList(service.getFavoritedLocations(userId));
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

    public Observable<Object> createImageImpression(String locationId, Bitmap bitmap) {
        File jpgFile = BitmapHelper.toJpgFile(bitmap);
        if (jpgFile == null) {
            return Observable.error(new Throwable("could not store image"));
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), jpgFile);
        return GenericErrorHandler.wrapSingle(service.postImageImpression(locationId, requestBody));
    }

    public Observable<Object> createVideoImpression(String locationId, byte[] videoData) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("video/3gpp"), videoData);
        return GenericErrorHandler.wrapSingle(service.postVideoImpression(locationId, requestBody));
    }

    public Observable<LocatorLocation> createLocation(String title,
                                                   double  lon,
                                                   double  lat,
                                                   String[]  categories,
                                                   Bitmap bitmap) {
        File jpgFile = BitmapHelper.toJpgFile(bitmap);
        if (jpgFile == null) {
            return Observable.error(new Throwable("could not store image"));
        }
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody lonBody   = RequestBody.create(MediaType.parse("text/plain"), Double.toString(lon));
        RequestBody latBody   = RequestBody.create(MediaType.parse("text/plain"), Double.toString(lat));
        RequestBody imgBody   = RequestBody.create(MediaType.parse("image/jpg"), jpgFile);
        return GenericErrorHandler.wrapSingle(service.createLocation(titleBody, lonBody, latBody,
                categories, imgBody));
    }

    public Observable<Object> createTextImpression(String locationId, String text) {
        TextImpressionRequest request = new TextImpressionRequest(text);
        return GenericErrorHandler.wrapSingle(service.postTextImpression(locationId, request));
    }
}
