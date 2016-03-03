package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.apiservice.Api;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class User implements LocatorObject, Serializable {

    @SerializedName("_id")
    public String id = "";

    @SerializedName("residence")
    public String residence = "";

    @SerializedName("mail")
    public String mail = "";

    @SerializedName("name")
    public String name = "";

    @SerializedName("picture")
    public String picture = "";

    @SerializedName("thumb")
    public String thumb = "";

    @SerializedName("following")
    public List<String> following = new LinkedList<>();

    @SerializedName("location_count")
    public int locationCount = 0;

    @SerializedName("follower_count")
    public int followerCount = 0;

    @Override
    public String thumbnailUri() {
        return Api.serverUrl + picture;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof User && ((User) other).id.equals(id);
    }
}
