package com.locator_app.locator.apiservice.locations;

import android.app.DownloadManager;
import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.Impression;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
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

        @Multipart
        @POST(Api.version + "/locations/{locationId}/impressions/image")
        Observable<Response<EchoResponse>> postImageImpression(@Path("locationId") String locationId,
            @Part("file\"; filename=impression.png") RequestBody file);

        @Multipart
        @POST(Api.version + "/dev/test/formData")
        Observable<Response<EchoResponse>> testFormData(
                @Part("file\"; filename=\"impression.jpg\"") RequestBody file);
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

    public class EchoResponse {
        /*
        @SerializedName("headers")
        public String headers;

        @SerializedName("payload")
        public String payload;*/
    }

    public Observable<EchoResponse> createImageImpression(String locationId, Bitmap bitmap) {
        File f = new File(LocatorApplication.getAppContext().getCacheDir(), "impression.png");
        try {
            f.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), f);
            return GenericErrorHandler.wrapSingle(service.postImageImpression(locationId, requestBody));
        } catch (IOException e) {
            e.printStackTrace();
            return Observable.error(e);
        }
    }
}
