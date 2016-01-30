package com.locator_app.locator.controller;


import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.device.DeviceApiService;
import com.locator_app.locator.apiservice.device.RegisterDeviceRequest;
import com.locator_app.locator.apiservice.device.RegisterDeviceResponse;

import rx.Observable;

public class RegisterDeviceController {

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

    private static RegisterDeviceController instance;
    public static RegisterDeviceController getInstance() {
        if (instance == null) {
            instance = new RegisterDeviceController();
        }
        return instance;
    }

}
