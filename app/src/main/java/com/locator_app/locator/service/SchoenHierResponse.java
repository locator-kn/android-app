package com.locator_app.locator.service;


import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.model.SchoenHier;

import java.util.List;

public class SchoenHierResponse {

    @SerializedName("results")
    public List<SchoenHierResult> results;

    @SerializedName("stats")
    public SchoenHierStats stats;

    public class SchoenHierResult {
        @SerializedName("dis")
        public double distance;

        @SerializedName("obj")
        public SchoenHier schoenHier;
    }

    public class SchoenHierStats {
        @SerializedName("nscanned")
        public int nScanned;

        @SerializedName("objectsLoaded")
        public int nLoaded;

        @SerializedName("avgDistance")
        public double averageDistance;

        @SerializedName("maxDistance")
        public double maxDistance;
    }
}
