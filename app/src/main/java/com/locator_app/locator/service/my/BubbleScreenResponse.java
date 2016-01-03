package com.locator_app.locator.service.my;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.Message;

import java.util.LinkedList;
import java.util.List;

public class BubbleScreenResponse {

    @SerializedName("messages")
    public List<Message> messages = new LinkedList<>();

    @SerializedName("locations")
    public List<LocationResult> locations = new LinkedList<>();

    public class LocationResult {

        @SerializedName("obj")
        public LocatorLocation location;

        @SerializedName("dis")
        public double distance;
    }

}
