package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class City implements Serializable {
    @SerializedName("title")
    public String title;

    @SerializedName("place_id")
    public String placeId;

    @SerializedName("id")
    public String id;
}
