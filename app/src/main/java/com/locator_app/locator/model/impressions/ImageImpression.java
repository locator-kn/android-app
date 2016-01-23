package com.locator_app.locator.model.impressions;

public class ImageImpression extends AbstractImpression {

    private final String imageUri;

    public ImageImpression(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }
}
