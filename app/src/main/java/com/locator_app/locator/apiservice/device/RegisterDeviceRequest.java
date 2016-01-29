package com.locator_app.locator.apiservice.device;

import com.google.gson.annotations.SerializedName;

public class RegisterDeviceRequest {

    @SerializedName("deviceId")
    public String deviceId = "";

    @SerializedName("type")
    public final String type = "android";

    @SerializedName("version")
    public String version = "";

    @SerializedName("deviceModel")
    public String deviceModel = "";

    @SerializedName("pushToken")
    public String pushToken = "";
}
