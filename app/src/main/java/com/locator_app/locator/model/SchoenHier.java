package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;

public class SchoenHier {

    @SerializedName("_id")
    public String id;

    @SerializedName("geotag")
    public GeoTag geoTag;

    @SerializedName("create_date")
    public String creationDate;

    @SerializedName("modified_date")
    public String modifiedDate;
}
