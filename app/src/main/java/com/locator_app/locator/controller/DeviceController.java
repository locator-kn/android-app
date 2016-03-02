package com.locator_app.locator.controller;


import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.device.DeviceApiService;
import com.locator_app.locator.apiservice.device.RegisterDeviceRequest;
import com.locator_app.locator.apiservice.device.RegisterDeviceResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeviceController {

    DeviceApiService service = new DeviceApiService();

    public Observable<Object> registerDevice(String pushToken) {
        RegisterDeviceRequest request = new RegisterDeviceRequest();
        request.version = Build.VERSION.RELEASE;
        request.deviceModel = Build.MODEL;
        request.manufacturer = Build.MANUFACTURER;
        request.deviceId = Settings.Secure.getString(LocatorApplication.getAppContext()
            .getContentResolver(), Settings.Secure.ANDROID_ID);
        request.pushToken = pushToken;
        return service.registerDevice(request)
                .doOnNext((registrationResponse) -> storePushToken(pushToken))
                .doOnError((throwable) ->
                        Log.d("DeviceController",
                              "Could not register device: " + throwable.getMessage()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void storePushToken(String token) {
        String SHARED_PREFERENCES_PUSH_TOKEN = "pushToken";
        SharedPreferences sharedPreferences = LocatorApplication.getSharedPreferences();
        sharedPreferences.edit().putString(SHARED_PREFERENCES_PUSH_TOKEN, token).apply();
    }

    private static DeviceController instance;
    public static DeviceController getInstance() {
        if (instance == null) {
            instance = new DeviceController();
        }
        return instance;
    }

}
