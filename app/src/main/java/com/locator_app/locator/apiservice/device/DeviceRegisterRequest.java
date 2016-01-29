package com.locator_app.locator.apiservice.device;

import com.google.gson.annotations.SerializedName;

public class DeviceRegisterRequest {

    @SerializedName("deviceId")
    public String deviceId = "";

    @SerializedName("type")
    public String type = "";

    @SerializedName("version")
    public String version = "";

    @SerializedName("deviceModel")
    public String deviceModel = "";

    @SerializedName("pushToken")
    public String pushToken = "";
}
