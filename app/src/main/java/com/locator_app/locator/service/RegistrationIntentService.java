package com.locator_app.locator.service;


import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.DeviceController;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("RegIntentService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            if (token != null && !token.isEmpty()) {
                DeviceController.getInstance().registerDevice(token)
                        .subscribe(
                                (val) -> {},
                                (err) -> {}
                        );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
