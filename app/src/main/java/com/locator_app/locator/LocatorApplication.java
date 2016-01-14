package com.locator_app.locator;


import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class LocatorApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getAppContext() {
        return context;
    }
}
