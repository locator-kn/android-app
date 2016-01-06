package com.locator_app.locator.view;


import android.content.Context;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RelativeLayout;

import com.locator_app.locator.R;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.Message;

public class BubbleViewHelper {

    public static BubbleView createLocationBubbleView(RelativeBubbleLayout layout, int priority) {
        String iconUri = "drawable://" + R.drawable.facebook_logo;
        int strokeColor = ContextCompat.getColor(layout.getContext(), R.color.borderRed);
        int fillColor = ContextCompat.getColor(layout.getContext(), R.color.innerRed);
        return createBubbleView(layout, priority, iconUri, strokeColor, fillColor);
    }

    public static BubbleView createMessageBubbleView(RelativeBubbleLayout layout, int priority) {
        String iconUri = "drawable://" + R.drawable.message;
        int strokeColor = ContextCompat.getColor(layout.getContext(), R.color.borderYellow);
        int fillColor = ContextCompat.getColor(layout.getContext(), R.color.innerYellow);
        return createBubbleView(layout, priority, iconUri, strokeColor, fillColor);
    }

    private static BubbleView createBubbleView(RelativeBubbleLayout layout, int priority, String iconUri, int strokeColor, int fillColor) {
        BubbleView bubbleView = new BubbleView(layout.getContext());
        layout.addView(bubbleView);
        bubbleView.setStrokeColor(strokeColor);
        int strokeWidth = BubbleViewHelper.getStrokeWidthForPriority(priority, layout);
        bubbleView.setStrokeWidth(strokeWidth);
        bubbleView.setFillColor(fillColor);
        int radius = BubbleViewHelper.getRadiusForPriority(priority, layout);
        layout.setBubbleRadius(bubbleView, radius);
        bubbleView.loadImage(iconUri);
        return bubbleView;
    }

    public static int getRadiusForPriority(int priority, RelativeBubbleLayout layout) {
        double widthFactor = 0;
        if (priority == 0) {
            widthFactor = 0.13;
        }
        if (priority == 1) {
            widthFactor = 0.10;
        }
        if (priority == 2) {
            widthFactor = 0.07;
        }
        return (int) (widthFactor * layout.getWidth());
    }

    public static int getStrokeWidthForPriority(int priority, RelativeBubbleLayout layout) {
        return (int) (layout.getWidth() * 0.013);
    }
}
