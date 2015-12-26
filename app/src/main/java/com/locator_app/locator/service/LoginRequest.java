package com.locator_app.locator.service;


import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("mail")
    public String mail;

    @SerializedName("password")
    public String password;
}
