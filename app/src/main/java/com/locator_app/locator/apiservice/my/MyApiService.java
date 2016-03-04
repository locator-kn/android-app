package com.locator_app.locator.apiservice.my;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import rx.Observable;

public class MyApiService {

    interface MyApi {

        @GET(Api.version + "/my/bubblescreen")
        Observable<Response<BubbleScreenResponse>> bubbleScreen();

        @PUT(Api.version + "/my/users/changePwd")
        Observable<Response<Object>> changePassword(@Body ChangePasswordRequest changePasswordRequest);
    }

    MyApi service = ServiceFactory.createService(MyApi.class);

    public Observable<BubbleScreenResponse> bubbleScreen() {
        return GenericErrorHandler.wrapSingle(service.bubbleScreen());
    }

    public Observable<Object> changePassword(String oldPassword, String newPassword) {
        return GenericErrorHandler.wrapSingle(service.changePassword(new ChangePasswordRequest(oldPassword, newPassword)));
    }


}
