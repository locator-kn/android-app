package com.locator_app.locator.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.locator_app.locator.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class BubbleView extends View {

    private Paint painter;

    private int fillColor;
    public void setFillColor(int fillColor) {
        if (fillColor != this.fillColor) {
            this.fillColor = fillColor;
            invalidate();
        }
    }

    private int radius;
    public void setRadius(int radius) {
        if (radius != this.radius) {
            this.radius = radius;
            roundAndSetIcon(originalIcon);
            updateLayoutParams();
        }
    }

    private int strokeWidth;
    public void setStrokeWidth(int strokeWidth) {
        if (strokeWidth != this.strokeWidth) {
            this.strokeWidth = strokeWidth;
            roundAndSetIcon(originalIcon);
            invalidate();
        }
    }

    private int strokeColor;
    public void setStrokeColor(int strokeColor) {
        if (strokeColor != this.strokeColor) {
            this.strokeColor = strokeColor;
            invalidate();
        }
    }

    private int shadowWidth;
    public void setShadowWidth(int shadowWidth) {
        if (shadowWidth != this.shadowWidth) {
            this.shadowWidth = shadowWidth;
            requestLayout();
        }
    }

    private Bitmap originalIcon;
    private Bitmap icon;
    private void roundAndSetIcon(Bitmap icon) {
        this.originalIcon = icon;
        final int iconSize = (radius-strokeWidth)*2;
        this.icon = BitmapHelper.getRoundBitmap(icon, iconSize);
        invalidate();
    }

    public void setIcon(Bitmap icon) {
        this.originalIcon = icon;
        this.icon = icon;
        invalidate();
    }

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        Resources.Theme resourceTheme = context.getTheme();
        TypedArray a = resourceTheme.obtainStyledAttributes(attrs, R.styleable.BubbleView, 0, 0);
        painter = new Paint(Paint.ANTI_ALIAS_FLAG);
        painter.setStyle(Paint.Style.FILL);
        fillColor = a.getColor(R.styleable.BubbleView_fillColor, Color.TRANSPARENT);
        radius = a.getInteger(R.styleable.BubbleView_radius, 100);
        strokeColor = a.getColor(R.styleable.BubbleView_strokeColor, Color.MAGENTA);
        strokeWidth = a.getInteger(R.styleable.BubbleView_strokeWidth, 10);
        shadowWidth = a.getInteger(R.styleable.BubbleView_shadowWidth, 0);
        a.recycle();
    }

    public void moveTo(float x, float y) {
        int radius = getRadius();
        setX(x - radius);
        setY(y - radius);
        requestLayout();
    }

    public Point getCenter() {
        return new Point((int)getX() + getRadius(), (int)getY() + getRadius());
    }

    private void updateLayoutParams() {

        int currentRadius = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 2;
        currentRadius = Math.min(currentRadius, radius);
        float x = getX() + currentRadius;
        float y = getY() + currentRadius;
        if (getLayoutParams() != null) {
            int newRadius = getRadius();
            getLayoutParams().width = newRadius * 2;
            getLayoutParams().height = newRadius * 2;
        }
        moveTo(x, y);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        updateLayoutParams();
    }

    public int getRadius() {
        return radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int x = getMeasuredWidth() / 2;
        final int y = getMeasuredHeight() / 2;

        painter.setStyle(Paint.Style.STROKE);
        painter.setColor(strokeColor);
        painter.setStrokeWidth(strokeWidth);
        final int shadowColor = 0x80000000;
        painter.setShadowLayer(shadowWidth, 0, 0, shadowColor);
        canvas.drawCircle(x, y, radius - strokeWidth/2, painter);

        if (icon == null) {
            painter.setStyle(Paint.Style.FILL);
            painter.setColor(fillColor);
            canvas.drawCircle(x, y, radius - strokeWidth, painter);
        } else {
            canvas.drawBitmap(icon, x - icon.getWidth()/2, y - icon.getHeight()/2, painter);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final int desiredSize = radius * 2;
        int width = desiredSize;
        int height = desiredSize;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredSize, widthSize);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredSize, heightSize);
        }

        setMeasuredDimension(width, height);
    }

    public void loadImage(String imageUri) {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration configuration =
                    new ImageLoaderConfiguration.Builder(getContext()).build();
            ImageLoader.getInstance().init(configuration);
        }
        ImageLoader.getInstance().loadImage(imageUri, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                roundAndSetIcon(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }
}
