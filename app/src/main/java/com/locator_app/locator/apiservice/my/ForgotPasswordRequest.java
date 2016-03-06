package com.locator_app.locator.apiservice.my;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequest {
    @SerializedName("mail")
    public String mail;

    public  ForgotPasswordRequest(String email) {
        mail = email;
    }
}
