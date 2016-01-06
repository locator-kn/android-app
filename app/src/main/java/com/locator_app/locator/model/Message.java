package com.locator_app.locator.model;

import com.google.gson.annotations.SerializedName;

public class Message {

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

    @SerializedName("message_type")
    public String messageType;

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Message && id.equals(((Message) other).id);
    }
}
