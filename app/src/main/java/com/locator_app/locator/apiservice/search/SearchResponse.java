package com.locator_app.locator.apiservice.search;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.apiservice.locations.LocationResponse;
import com.locator_app.locator.model.GoogleLocation;

import java.util.LinkedList;
import java.util.List;

public class SearchResponse {

    @SerializedName("google")
    public List<GoogleLocation> googleLocations = new LinkedList<>();

    @SerializedName("locator")
    public List<LocationResponse> locatorLocations = new LinkedList<>();

}
