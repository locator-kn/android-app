package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class LocatorLocation {

    @SerializedName("_id")
    public String id;

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
    public City city;

    @SerializedName("public")
    public boolean isPublic;

    @SerializedName("geotag")
    public GeoTag geoTag;

    @SerializedName("delete")
    public boolean deleted;

    @SerializedName("images")
    public Images images;

}
