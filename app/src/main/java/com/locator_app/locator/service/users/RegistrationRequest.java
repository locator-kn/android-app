package com.locator_app.locator.service.users;


import com.google.gson.annotations.SerializedName;

public class RegistrationRequest {

    @SerializedName("mail")
    public String mail;

    @SerializedName("password")
    public String password;

    @SerializedName("name")
    public String name;

    @SerializedName("residence")
    public String residence;

}
