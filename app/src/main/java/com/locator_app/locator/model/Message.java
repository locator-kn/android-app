package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;
import com.locator_app.locator.R;

public class Message implements LocatorObject {

    @SerializedName("_id")
    public String id;

    @SerializedName("conversation_id")
    public String conversationId;

    @SerializedName("from")
    public String from;

    @SerializedName("message")
    public String message;

    @SerializedName("timestamp")
    public long timestamp;

    @SerializedName("modified_date")
    public String modifiedDate;

    @SerializedName("create_date")
    public String createDate;

    @SerializedName("message_type")
    public String messageType;

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Message && id.equals(((Message) other).id);
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String thumbnailUri() {
        return "drawable://" + R.drawable.message;
    }
}
