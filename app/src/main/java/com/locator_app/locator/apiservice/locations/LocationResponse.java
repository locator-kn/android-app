package com.locator_app.locator.apiservice.locations;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.model.LocatorLocation;

public class LocationResponse {

    @SerializedName("obj")
    public LocatorLocation location;

    @SerializedName("dis")
    public double distance;

}
