package com.locator_app.locator.model.impressions;

public class TextImpression extends AbstractImpression {

    private final String text;

    public TextImpression(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
