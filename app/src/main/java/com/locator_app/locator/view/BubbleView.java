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

    private Point center;
    private int radius;
    private Paint painter;

    private int fillColor;
    public void setFillColor(int fillColor) {
        if (fillColor != this.fillColor) {
            this.fillColor = fillColor;
            invalidate();
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
            invalidate();
        }
    }

    private Bitmap originalIcon;
    private Bitmap icon;
    private void roundAndSetIcon(Bitmap icon) {
        if (icon != null) {
            this.originalIcon = icon;
            int iconSize = (radius - strokeWidth - shadowWidth) * 2;
            this.icon = BitmapHelper.getRoundBitmap(icon, iconSize);
            invalidate();
        }
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
        strokeColor = a.getColor(R.styleable.BubbleView_strokeColor, Color.MAGENTA);
        strokeWidth = a.getInteger(R.styleable.BubbleView_strokeWidth, 0);
        shadowWidth = a.getInteger(R.styleable.BubbleView_shadowWidth, 0);
        center = new Point();
        a.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int x = center.x;
        final int y = center.y;

        final int shadowColor = 0x80000000;
        painter.setShadowLayer(shadowWidth, 0, 0, shadowColor);
        painter.setStrokeWidth(strokeWidth);
        painter.setColor(strokeColor);
        painter.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(x, y, radius - shadowWidth - strokeWidth, painter);

        painter.setStyle(Paint.Style.FILL);
        if (icon == null) {
            painter.setColor(fillColor);
            canvas.drawCircle(x, y, radius - shadowWidth - strokeWidth, painter);
        } else {
            canvas.drawBitmap(icon, strokeWidth + shadowWidth, strokeWidth + shadowWidth, painter);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        int width, height;

        int contentWidth = 200;
        int contentHeight = 200;
        width = getMeasurement(widthMeasureSpec, contentWidth);
        height = getMeasurement(heightMeasureSpec, contentHeight);

        roundAndSetIcon(originalIcon);

        setMeasuredDimension(width, height);
    }

    private int getMeasurement(int measureSpec, int contentSize) {
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.AT_MOST:
                return Math.min(specSize, contentSize);
            case MeasureSpec.UNSPECIFIED:
                return contentSize;
            case MeasureSpec.EXACTLY:
                return specSize;
            default:
                return 0;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h,
                                 int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            //If there was a change, reset the parameters
            center.x = w / 2;
            center.y = h / 2;
            radius = Math.min(center.x, center.y);
        }
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
