package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.R;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class LocatorLocation implements LocatorObject, Serializable {

    @SerializedName("_id")
    public String id;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("preLocation")
    public boolean prelocation;

    @SerializedName("create_date")
    public String createDate;

    @SerializedName("modified_date")
    public String modifiedDate;

    @SerializedName("tags")
    public List<String> tags = new LinkedList<>();

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("city")
    public City city = new City();

    @SerializedName("public")
    public boolean isPublic;

    @SerializedName("geotag")
    public GeoTag geoTag;

    @SerializedName("delete")
    public boolean deleted;

    @SerializedName("images")
    public Images images = new Images();

    @SerializedName("favorites")
    public List<String> favorites = new LinkedList<>();

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof LocatorLocation && id.equals(((LocatorLocation)other).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public String thumbnailUri() {
        if (images == null || images.small == null) {
            return "drawable://" + R.drawable.locator_app_icon;
        }
        return images.getSmall();
    }
}
