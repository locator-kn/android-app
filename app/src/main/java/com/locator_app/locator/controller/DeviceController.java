package com.locator_app.locator.controller;


import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.PersistentCookieStore;
import com.locator_app.locator.apiservice.device.DeviceApiService;
import com.locator_app.locator.apiservice.device.RegisterDeviceRequest;
import com.locator_app.locator.apiservice.device.RegisterDeviceResponse;
import com.locator_app.locator.apiservice.users.RegistrationResponse;

import rx.Observable;

public class DeviceController {

    DeviceApiService service = new DeviceApiService();

    public Observable<RegisterDeviceResponse> registerDevice(String pushToken) {
        if (deviceAlreadyRegistered()) {
            return Observable.just(new RegisterDeviceResponse());
        }
        RegisterDeviceRequest request = new RegisterDeviceRequest();
        request.version = Build.VERSION.RELEASE;
        request.deviceModel = Build.MODEL;
        request.manufacturer = Build.MANUFACTURER;
        request.deviceId = Settings.Secure.getString(LocatorApplication.getAppContext()
            .getContentResolver(), Settings.Secure.ANDROID_ID);
        request.pushToken = pushToken;
        return service.registerDevice(request);
    }

    private boolean deviceAlreadyRegistered() {
        SharedPreferences prefs = LocatorApplication.getSharedPreferences();
        String deviceCookie = prefs.getString(PersistentCookieStore.LOCATOR_DEVICE_COOKIE, "");
        boolean isDeviceCookieSet = !deviceCookie.isEmpty();
        return isDeviceCookieSet;
    }

    private static DeviceController instance;
    public static DeviceController getInstance() {
        if (instance == null) {
            instance = new DeviceController();
        }
        return instance;
    }

}
