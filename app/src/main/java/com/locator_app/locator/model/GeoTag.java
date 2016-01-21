package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class GeoTag implements Serializable {

    @SerializedName("coordinates")
    public List<Double> coordinates = Arrays.asList(0., 0.);

    @SerializedName("type")
    public String type = "";

    public double getLongitude() {
        final int GEOJSON_INDEX_LONGITUDE = 0;
        return coordinates.get(GEOJSON_INDEX_LONGITUDE);
    }

    public double getLatitude() {
        final int GEOJSON_INDEX_LATITUDE = 1;
        return coordinates.get(GEOJSON_INDEX_LATITUDE);
    }
}
