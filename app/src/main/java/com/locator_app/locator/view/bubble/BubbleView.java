package com.locator_app.locator.view.bubble;

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
import com.locator_app.locator.util.BitmapHelper;
import com.locator_app.locator.util.CacheImageLoader;

public class BubbleView extends View {

    private Point center = new Point(0, 0);
    private int radius = 80;

    private Paint painter = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint bitmapPainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int fillColor = Color.TRANSPARENT;
    public void setFillColor(int fillColor) {
        if (this.fillColor != fillColor) {
            this.fillColor = fillColor;
            invalidate();
        }
    }

    private int strokeWidth = 0;
    public void setStrokeWidth(int strokeWidth) {
        if (this.strokeWidth != strokeWidth) {
            this.strokeWidth = strokeWidth;
            roundAndSetIcon(originalIcon);
            invalidate();
        }
    }

    private int strokeColor = Color.TRANSPARENT;
    public void setStrokeColor(int strokeColor) {
        if (this.strokeColor != strokeColor) {
            this.strokeColor = strokeColor;
            invalidate();
        }
    }

    private int shadowWidth = 0;
    public void setShadowWidth(int shadowWidth) {
        if (this.shadowWidth != shadowWidth) {
            this.shadowWidth = shadowWidth;
            invalidate();
        }
    }

    private String imageUri = "";
    private Bitmap originalIcon;
    private Bitmap icon;
    private void roundAndSetIcon(Bitmap icon) {
        if (icon != null) {
            this.originalIcon = icon;
            int iconSize = (radius-strokeWidth-shadowWidth) * 2;
            this.icon = BitmapHelper.getRoundBitmap(icon, iconSize);
            invalidate();
        }
    }

    public void setIcon(Bitmap icon) {
        if (icon != originalIcon && this.icon != icon) {
            this.originalIcon = icon;
            this.icon = icon;
            invalidate();
        }
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
        setFillColor(a.getColor(R.styleable.BubbleView_fillColor, Color.TRANSPARENT));
        setStrokeColor(a.getColor(R.styleable.BubbleView_strokeColor, Color.TRANSPARENT));
        setStrokeWidth(a.getInteger(R.styleable.BubbleView_strokeWidth, 0));
        setShadowWidth(a.getInteger(R.styleable.BubbleView_shadowWidth, 0));
        a.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int shadowColor = 0x80000000;
        painter.setShadowLayer(shadowWidth, 0, 0, shadowColor);
        painter.setStrokeWidth(strokeWidth);
        painter.setColor(strokeColor);
        painter.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(center.x, center.y, radius - shadowWidth - strokeWidth, painter);

        painter.setStyle(Paint.Style.FILL);
        if (fillColor != Color.TRANSPARENT) {
            painter.setColor(fillColor);
            canvas.drawCircle(center.x, center.y, radius - shadowWidth - strokeWidth, painter);
        }

        if (icon != null) {
            int distance = strokeWidth + shadowWidth;
            int left = center.x - radius + distance;
            int top = center.y - radius + distance;
            canvas.drawBitmap(icon, left, top, bitmapPainter);
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

            roundAndSetIcon(originalIcon);
        }
    }

    @Override
    protected void	onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.originalIcon != null) {
            roundAndSetIcon(this.originalIcon);
        }
    }

    public void loadImage(String imageUri) {
        loadImage(imageUri, "");
    }

    public void loadImage(String imageUri, String fallbackImageUri) {
        if (!this.imageUri.equals(imageUri)) {
            this.imageUri = imageUri;
            CacheImageLoader.getInstance().loadAsync(imageUri)
                    .subscribe(
                            (bitmap) -> roundAndSetIcon(bitmap),
                            (error) -> loadFallbackImage(fallbackImageUri)
                    );
        }
    }

    private void loadFallbackImage(String fallbackImageUri) {
        if (!fallbackImageUri.isEmpty()) {
            CacheImageLoader.getInstance().loadAsync(fallbackImageUri)
                    .subscribe(
                            (fallbackBitmap) -> roundAndSetIcon(fallbackBitmap),
                            (error) -> {}
                    );
        }
    }
}
