package com.locator_app.locator.service.locations;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.model.LocatorLocation;

import java.util.LinkedList;
import java.util.List;

public class LocationsNearbyResponse {

    @SerializedName("results")
    public List<Result> results = new LinkedList<>();

    public class Result {

        @SerializedName("obj")
        public LocatorLocation location;

        @SerializedName("dis")
        public double distance;
    }
}
