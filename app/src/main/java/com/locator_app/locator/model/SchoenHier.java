package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.util.StringToDateConverter;

import java.util.ArrayList;
import java.util.Date;

public class SchoenHier {

    @SerializedName("_id")
    public String id;

    @SerializedName("geotag")
    public GeoTag geoTag;

    @SerializedName("create_date")
    public String creationDate;

    @SerializedName("modified_date")
    public String modifiedDate;

    public Date getCreationDate() {
        return StringToDateConverter.toDate(creationDate);
    }

    public Date getModifiedDate() {
        return StringToDateConverter.toDate(modifiedDate);
    }
}
