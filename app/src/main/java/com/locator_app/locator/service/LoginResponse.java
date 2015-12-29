package com.locator_app.locator.service;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("_id")
    public String _id;

    @SerializedName("mail")
    public String mail;

    @SerializedName("name")
    public String name;

    @SerializedName("residence")
    public String residence;

}
