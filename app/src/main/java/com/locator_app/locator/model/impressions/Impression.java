package com.locator_app.locator.model.impressions;

import com.google.gson.annotations.SerializedName;

public class Impression {

    @SerializedName("_id")
    public String id = "";

    @SerializedName("user_id")
    public String userId = "";

    @SerializedName("location_id")
    public String locationId = "";

    @SerializedName("data")
    public String data = "";

    @SerializedName("type")
    public String type = "";

    @SerializedName("file_id")
    public String fileId = "";

    @SerializedName("create_date")
    public String createDate = "";

    @SerializedName("modified_date")
    public String modifiedDate = "";

}
