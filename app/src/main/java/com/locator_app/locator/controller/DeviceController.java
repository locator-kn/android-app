package com.locator_app.locator.controller;


import android.os.Build;
import android.provider.Settings;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.device.DeviceApiService;
import com.locator_app.locator.apiservice.device.RegisterDeviceRequest;
import com.locator_app.locator.apiservice.device.RegisterDeviceResponse;

import rx.Observable;

public class DeviceController {

    DeviceApiService service = new DeviceApiService();

    public Observable<RegisterDeviceResponse> registerDevice() {
        RegisterDeviceRequest request = new RegisterDeviceRequest();
        request.version = Build.VERSION.RELEASE;
        request.deviceModel = Build.MODEL;
        request.manufacturer = Build.MANUFACTURER;
        request.deviceId = Settings.Secure.getString(LocatorApplication.getAppContext()
        .getContentResolver(), Settings.Secure.ANDROID_ID);
        request.pushToken = "todo";
        return service.registerDevice(request);
    }

    private static DeviceController instance;
    public static DeviceController getInstance() {
        if (instance == null) {
            instance = new DeviceController();
        }
        return instance;
    }

}
