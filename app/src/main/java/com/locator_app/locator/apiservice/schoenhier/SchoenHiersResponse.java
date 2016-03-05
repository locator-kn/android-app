package com.locator_app.locator.apiservice.schoenhier;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.model.GeoTag;
import com.locator_app.locator.model.SchoenHier;

public class SchoenHiersResponse {

    @SerializedName("dis")
    public double distance = 0;

    @SerializedName("obj")
    public SchoenHier schoenHier = new SchoenHier();

}
