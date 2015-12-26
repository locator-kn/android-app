package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("nscanned")
    public int nScanned;

    @SerializedName("objectsLoaded")
    public int nLoaded;

    @SerializedName("avgDistance")
    public double averageDistance;

    @SerializedName("maxDistance")
    public double maxDistance;

    @SerializedName("time")
    public int time;
}
