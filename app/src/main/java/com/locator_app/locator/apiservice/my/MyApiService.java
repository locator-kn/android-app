package com.locator_app.locator.apiservice.my;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;

import retrofit.Response;
import retrofit.http.GET;
import rx.Observable;

public class MyApiService {

    interface MyApi {

        @GET(Api.version + "/my/bubblescreen")
        Observable<Response<BubbleScreenResponse>> bubbleScreen();
    }

    MyApi service = ServiceFactory.createService(MyApi.class);

    public Observable<BubbleScreenResponse> bubbleScreen() {
        return GenericErrorHandler.wrapSingle(service.bubbleScreen());
    }

}
