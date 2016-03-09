package com.locator_app.locator.view.bubble;


import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
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

    public int getBubbleRadius(View bubble) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bubble.getLayoutParams();
        int width = params.width;
        int height = params.height;
        return Math.min(width, height) / 2;
    }

    public Point getBubbleCenter(View bubble) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bubble.getLayoutParams();
        int radius = getBubbleRadius(bubble);
        int centerX = params.leftMargin + radius;
        int centerY = params.topMargin + radius;
        return new Point(centerX, centerY);
    }

    public void setBubbleRadius(View bubble, int radius) {
        Point center = getBubbleCenter(bubble);
        updateBubble(bubble, radius, center.x, center.y);
    }

    public void setBubbleCenter(View bubble, double percentScreenWidth, double percentScreenHeight) {
        int x = (int) (this.getWidth() * percentScreenWidth);
        int y = (int) (this.getHeight() * percentScreenHeight);
        setBubbleCenter(bubble, x, y);
    }

    public void setBubbleCenter(View bubble, int centerX, int centerY) {
        int radius = getBubbleRadius(bubble);
        updateBubble(bubble, radius, centerX, centerY);
    }

    private void updateBubble(View bubble, int radius, int centerX, int centerY) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bubble.getLayoutParams();
        params.width = radius * 2;
        params.height = radius * 2;
        params.leftMargin = centerX - radius;
        params.topMargin = centerY - radius;
        if (bubble instanceof BubbleView) {
            ((BubbleView)(bubble)).radius = radius;
        }
        bubble.setLayoutParams(params);
    }

}
