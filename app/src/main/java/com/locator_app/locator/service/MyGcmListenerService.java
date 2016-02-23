package com.locator_app.locator.service;


import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.locator_app.locator.R;

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {

        // http://developer.android.com/guide/topics/ui/notifiers/notifications.html
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.message)
                        .setLights(Color.GREEN, 1000, 2000)
                        .setContentTitle(data.getString("title"))
                        .setContentText(data.getString("message"));
        // Creates an explicit intent for an Activity in your app

        /*Intent resultIntent = new Intent(this, ResultActivity.class);*/

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);*/
        // Adds the back stack for the Intent (but not the Intent itself)
        /*stackBuilder.addParentStack(ResultActivity.class);*/
        // Adds the Intent that starts the Activity to the top of the stack
        /*stackBuilder.addNextIntent(resultIntent);*/
        /*PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );*/
        /*mBuilder.setContentIntent(resultPendingIntent);*/
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(5, mBuilder.build());
    }
}
