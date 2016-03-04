package com.locator_app.locator.apiservice.users;


import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("mail")
    public String mail;

    @SerializedName("password")
    public String password;

    public static LoginRequest fromRegistrationRequest(RegistrationRequest registrationRequest) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.mail = registrationRequest.mail;
        loginRequest.password = registrationRequest.password;
        return loginRequest;
    }
}