package com.locator_app.locator.apiservice.schoenhier;


import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.model.SchoenHier;

import java.util.List;

public class SchoenHiersNearbyResponse {

    @SerializedName("results")
    public List<SchoenHierResult> results;


    public class SchoenHierResult {
        @SerializedName("dis")
        public double distance;

        @SerializedName("obj")
        public SchoenHier schoenHier;
    }

}
