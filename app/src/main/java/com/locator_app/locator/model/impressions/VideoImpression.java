package com.locator_app.locator.model.impressions;


public class VideoImpression extends AbstractImpression {

    private final String videoUri;

    public VideoImpression(final String videoUri) {
        this.videoUri = videoUri;
    }

    public String getVideoUri() {
        return videoUri;
    }
}
