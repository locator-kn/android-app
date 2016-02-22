package com.locator_app.locator.service;


import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("fff", "From: " + from);
        Log.d("fff", "Message: " + message);
    }

}
