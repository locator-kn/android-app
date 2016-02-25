package com.locator_app.locator.apiservice.search;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.model.GoogleLocation;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.SchoenHier;

import java.util.LinkedList;
import java.util.List;

public class SearchResponse {
    @SerializedName("google")
    public List<GoogleLocation> googleLocations = new LinkedList<>();

    @SerializedName("locator")
    public List<LocatorLocation> locatorLocations = new LinkedList<>();
}
