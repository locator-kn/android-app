package com.locator_app.locator.view.bubble;


import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RelativeBubbleLayout extends RelativeLayout {

    public RelativeBubbleLayout(Context context) {
        super(context);
    }

    public RelativeBubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeBubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getBubbleRadius(BubbleView bubble) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bubble.getLayoutParams();
        int width = params.width;
        int height = params.height;
        return Math.min(width, height) / 2;
    }

    public Point getBubbleCenter(BubbleView bubble) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bubble.getLayoutParams();
        int radius = getBubbleRadius(bubble);
        int centerX = params.leftMargin + radius;
        int centerY = params.topMargin + radius;
        return new Point(centerX, centerY);
    }

    public void setBubbleRadius(BubbleView bubble, int radius) {
        Point center = getBubbleCenter(bubble);
        updateBubble(bubble, radius, center.x, center.y);
    }

    public void setBubbleCenter(BubbleView bubble, double percentScreenWidth, double percentScreenHeight) {
        int x = (int) (this.getWidth() * percentScreenWidth);
        int y = (int) (this.getHeight() * percentScreenHeight);
        setBubbleCenter(bubble, x, y);
    }

    public void setBubbleCenter(BubbleView bubble, int centerX, int centerY) {
        int radius = getBubbleRadius(bubble);
        updateBubble(bubble, radius, centerX, centerY);
    }

    private void updateBubble(BubbleView bubble, int radius, int centerX, int centerY) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bubble.getLayoutParams();
        params.width = radius * 2;
        params.height = radius * 2;
        params.leftMargin = centerX - radius;
        params.topMargin = centerY - radius;
        bubble.radius = radius;
        bubble.setLayoutParams(params);
    }

}
