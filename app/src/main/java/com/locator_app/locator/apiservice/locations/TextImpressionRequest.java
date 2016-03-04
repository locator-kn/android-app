package com.locator_app.locator.apiservice.locations;


import com.google.gson.annotations.SerializedName;

public class TextImpressionRequest {

    @SerializedName("data")
    public String data = "";

    public TextImpressionRequest(String text) {
        data = text;
    }
}
