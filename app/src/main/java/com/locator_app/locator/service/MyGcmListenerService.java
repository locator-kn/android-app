package com.locator_app.locator.service;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.view.LocationDetailActivity;
import com.locator_app.locator.view.profile.ProfileActivity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MyGcmListenerService extends GcmListenerService {

    // http://developer.android.com/guide/topics/ui/notifiers/notifications.html

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN  && validData(data)) {
            String title = data.getString("title", "");
            String message = data.getString("message", "");
            String entity = data.getString("entity", "");
            String entity_id = data.getString("entity_id", "");
            if (entity.equals("location")) {
                handleLocationNotification(title, message, entity_id);
            } else if (entity.equals("user")) {
                handleUserNotification(title, message, entity_id);
            }
        }
    }

    private boolean validData(Bundle data) {
        return  data.containsKey("title") &&
                data.containsKey("message") &&
                data.containsKey("entity") &&
                data.containsKey("entity_id");
    }

    private void handleUserNotification(String title, String message, String entity_id) {
        UserController.getInstance().getUser(entity_id)
                .subscribe(
                        (user) -> {
                            Map<String, Serializable> args = new HashMap<>();
                            args.put("profile", user);
                            startIntentOnClick(title, message, ProfileActivity.class, args);
                        },
                        (err) -> {
                        }
                );
    }

    private void handleLocationNotification(String title, String message, String entity_id) {
        LocationController.getInstance().getLocationById(entity_id)
                .subscribe(
                        (location) -> {
                            Map<String, Serializable> args = new HashMap<>();
                            args.put("location", location);
                            startIntentOnClick(title, message, LocationDetailActivity.class, args);
                        },
                        (err) -> {
                        }
                );
    }

    void startIntentOnClick(String title, String message, Class<?> intentClass, Map<String, Serializable> data) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.go_to_bubblescreen_small)
                        .setLights(Color.GREEN, 500, 500)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true);

        Intent intent = new Intent(this, intentClass);
        for (String key: data.keySet()) {
            intent.putExtra(key, data.get(key));
        }
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(intentClass);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                        //PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(5, builder.build());
    }


}
