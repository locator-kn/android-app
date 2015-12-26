package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("title")
    public String title;

    @SerializedName("place_id")
    public String placeId;

    @SerializedName("id")
    public String id;
}
