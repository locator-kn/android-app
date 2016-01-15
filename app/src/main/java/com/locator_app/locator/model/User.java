package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.Api;

import java.io.Serializable;

public class User implements LocatorObject, Serializable {

    @SerializedName("_id")
    public String _id = "";

    @SerializedName("birthdate")
    public String birthdate = "";

    @SerializedName("residence")
    public String residence = "";

    @SerializedName("description")
    public String description = "";

    @SerializedName("mail")
    public String mail = "";

    @SerializedName("surname")
    public String surname = "";

    @SerializedName("name")
    public String name = "";

    @SerializedName("picture")
    public String picture = "";

    @SerializedName("thumb")
    public String thumb = "";

    public boolean loggedIn = false;

    @Override
    public String thumbnailUri() {
        if (thumb.isEmpty()) {
            return "drawable://" + R.drawable.profile;
        }
        return Api.serverUrl + thumb;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            return ((User) other)._id.equals(_id);
        }
        return false;
    }
}
