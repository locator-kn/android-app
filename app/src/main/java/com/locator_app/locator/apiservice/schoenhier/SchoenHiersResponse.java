package com.locator_app.locator.apiservice.schoenhier;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.model.GeoTag;

public class SchoenHiersResponse {

    @SerializedName("geotag")
    public GeoTag geoTag;

    @SerializedName("create_date")
    public String createDate;

    @SerializedName("modified_date")
    public String modifiedDate;

    @SerializedName("_id")
    public String _id;

}
