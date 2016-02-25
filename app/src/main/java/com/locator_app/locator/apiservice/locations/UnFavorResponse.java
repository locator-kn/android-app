package com.locator_app.locator.apiservice.locations;

import com.google.gson.annotations.SerializedName;

public class UnFavorResponse {
    @SerializedName("favorites")
    public Integer favorites = 0;

    @SerializedName("added")
    public Boolean added = false;

    @SerializedName("removed")
    public Boolean removed = false;
}
