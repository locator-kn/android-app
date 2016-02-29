package com.locator_app.locator.view.impressions;


import com.locator_app.locator.model.impressions.AbstractImpression;

public interface ImpressionObserver {

    void onImpressionCreated(AbstractImpression.ImpressionType type);

    void onImpressionCreationFailed(AbstractImpression.ImpressionType type, Throwable error);

}
