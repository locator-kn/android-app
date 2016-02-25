package com.locator_app.locator.model;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;

import java.util.LinkedList;
import java.util.List;

public class GoogleLocation extends LocatorLocation {
    @Override
    public String thumbnailUri() {
        return "android.resource://" + LocatorApplication.getAppContext().getPackageName()
                + "/" + R.drawable.common_google_signin_btn_icon_dark_normal;
    }
}
