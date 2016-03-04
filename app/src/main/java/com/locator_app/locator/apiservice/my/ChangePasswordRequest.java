package com.locator_app.locator.apiservice.my;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("old_password")
    public String old_password;

    @SerializedName("new_password")
    public String new_password;

    public  ChangePasswordRequest(String oldPassword, String newPassword) {
        old_password = oldPassword;
        new_password = newPassword;
    }
}
