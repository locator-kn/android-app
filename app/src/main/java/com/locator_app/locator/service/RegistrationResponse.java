package com.locator_app.locator.service;

import com.google.gson.annotations.SerializedName;

public class RegistrationResponse {

    @SerializedName("_id")
    String _id;

    @SerializedName("name")
    String name;

    @SerializedName("mail")
    String mail;

    @SerializedName("residence")
    String residence;

    @SerializedName("statusCode")
    int statusCode;

    @SerializedName("error")
    String error;

    @SerializedName("message")
    String message;
}
