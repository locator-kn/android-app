package com.locator_app.locator.apiservice.my;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;
import rx.Observable;

public class MyApiService {

    interface MyApi {

        @GET(Api.version + "/my/bubblescreen")
        Observable<Response<BubbleScreenResponse>> bubbleScreen(@Query("long") double lon,
                                                                @Query("lat") double lat);

        @PUT(Api.version + "/my/users/changePwd")
        Observable<Response<Object>> changePassword(@Body ChangePasswordRequest changePasswordRequest);
    }

    MyApi service = ServiceFactory.createService(MyApi.class);

    public Observable<BubbleScreenResponse> bubbleScreen(double lon, double lat) {
        return GenericErrorHandler.wrapSingle(service.bubbleScreen(lon, lat));
    }

    public Observable<Object> changePassword(String oldPassword, String newPassword) {
        return GenericErrorHandler.wrapSingle(service.changePassword(new ChangePasswordRequest(oldPassword, newPassword)));
    }


}
