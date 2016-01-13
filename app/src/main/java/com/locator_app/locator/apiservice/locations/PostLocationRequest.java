package com.locator_app.locator.apiservice.locations;


import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class PostLocationRequest {

    @SerializedName("title")
    public String title;

    @SerializedName("long")
    public double lon;

    @SerializedName("lat")
    public double lat;

    @SerializedName("description")
    public String description;

    @SerializedName("categories")
    public List<String> categories = new LinkedList<>();

}
