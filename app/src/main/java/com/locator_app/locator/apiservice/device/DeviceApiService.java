package com.locator_app.locator.apiservice.device;


import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public class DeviceApiService {

    interface DeviceApi {
        @POST(Api.version + "/device/register")
        Observable<Response<DeviceRegisterResponse>> registerDevice(@Body DeviceRegisterRequest request);
    }

    DeviceApi service = ServiceFactory.createService(DeviceApi.class);

    public Observable<DeviceRegisterResponse> registerDevice(DeviceRegisterRequest request) {
        return service.registerDevice(request)
                .flatMap(this::parseResponse);
    }

    private Observable<DeviceRegisterResponse> parseResponse(Response<DeviceRegisterResponse> response) {
        if (response.code() == 201) {
            return Observable.just(response.body());
        }
        return Observable.error(new Exception("could not register device"));
    }

}
