package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.Api;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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

    @SerializedName("following")
    public List<String> following = new LinkedList<>();

    @Override
    public String thumbnailUri() {
        return Api.serverUrl + picture;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof User && ((User) other)._id.equals(_id);
    }
}
