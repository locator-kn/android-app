package com.locator_app.locator.controller;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.device.DeviceApiService;
import com.locator_app.locator.apiservice.device.RegisterDeviceRequest;
import com.locator_app.locator.apiservice.device.RegisterDeviceResponse;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.service.RegistrationIntentService;

import org.xml.sax.Locator;

import rx.Observable;
import rx.functions.Action0;

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
        return service.registerDevice(request)
                .doOnCompleted(this::onDeviceRegistered);
    }

    private void onDeviceRegistered() {
        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        preferences.edit().putBoolean("pushtoken", true).apply();
    }

    private boolean deviceAlreadyRegistered() {
        return LocatorApplication.getSharedPreferences().getBoolean("pushtoken", false);
    }

    private static DeviceController instance;
    public static DeviceController getInstance() {
        if (instance == null) {
            instance = new DeviceController();
        }
        return instance;
    }

}
