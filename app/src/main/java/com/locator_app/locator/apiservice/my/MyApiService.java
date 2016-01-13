package com.locator_app.locator.apiservice.my;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;

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
        return service.bubbleScreen()
                .flatMap(this::parseBubbleScreenResponse);
    }

    private Observable<BubbleScreenResponse> parseBubbleScreenResponse(Response response) {
        if (response.isSuccess()) {
            return Observable.just((BubbleScreenResponse)response.body());
        }
        return Observable.error(new Exception("http-error: " + Integer.toString(response.code())));
    }
}
