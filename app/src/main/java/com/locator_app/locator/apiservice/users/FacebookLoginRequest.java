package com.locator_app.locator.apiservice.users;

import com.google.gson.annotations.SerializedName;

public class FacebookLoginRequest {

    @SerializedName("token")
    public String token;

    public FacebookLoginRequest(String token) {
        this.token = token;
    }
}
