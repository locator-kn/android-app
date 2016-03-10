package com.locator_app.locator.apiservice.my;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;
import rx.Observable;

public class MyApiService {

    interface MyApi {

        @GET(Api.version + "/my/bubblescreen")
        Observable<Response<BubbleScreenResponse>> bubbleScreen(@Query("long") double lon,
                                                                @Query("lat") double lat,
                                                                @Query("maxDistance") double maxDistance,
                                                                @Query("limit") int limit);

        @PUT(Api.version + "/my/users/changePwd")
        Observable<Response<Object>> changePassword(@Body ChangePasswordRequest changePasswordRequest);

        @POST(Api.version + "/my/users/forgetPassword")
        Observable<Response<Object>> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);
    }

    MyApi service = ServiceFactory.createService(MyApi.class);

    public Observable<BubbleScreenResponse> bubbleScreen(double lon, double lat) {
        final double maxDistance = 30000; // in km
        final int limit = 20;
        return GenericErrorHandler.wrapSingle(service.bubbleScreen(lon, lat, maxDistance, limit));
    }

    public Observable<Object> changePassword(String oldPassword, String newPassword) {
        return GenericErrorHandler.wrapSingle(service.changePassword(new ChangePasswordRequest(oldPassword, newPassword)));
    }

    public Observable<Object> forgotPassword(String email) {
        return GenericErrorHandler.wrapSingle(service.forgotPassword(new ForgotPasswordRequest(email)));
    }
}
