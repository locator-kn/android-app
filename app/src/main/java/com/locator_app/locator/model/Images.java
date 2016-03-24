package com.locator_app.locator.model;


import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.apiservice.Api;

import java.io.Serializable;

public class Images implements Serializable {

    @SerializedName("googlemap")
    public String googlemap = "";
    public String getGooglemap() {
        return get(googlemap);
    }

    @SerializedName("xlarge")
    public String xlarge = "";
    public String getXLarge() {
        return get(xlarge);
    }

    @SerializedName("large")
    public String large = "";
    public String getLarge() {
        return get(large);
    }

    @SerializedName("normal")
    public String normal = "";
    public String getNormal() {
        return get(normal);
    }

    @SerializedName("small")
    public String small = "";
    public String getSmall() {
        return get(small);
    }

    @SerializedName("egg")
    public String egg = "";
    public String getEgg() {
        return get(egg);
    }
    public boolean hasEgg() {
        return egg.isEmpty();
    }

    private String get(String size) {
        return Api.serverUrl + size;
    }

}
