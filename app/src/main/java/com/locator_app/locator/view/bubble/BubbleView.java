package com.locator_app.locator.view.bubble;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.locator_app.locator.R;
import com.locator_app.locator.util.BitmapHelper;

import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BubbleView extends View {

    private Point center = new Point(0, 0);
    int radius = 80;

    private Paint painter = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint bitmapPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String imageUri;
    private Bitmap icon;

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
            setImage(imageUri);
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
            setImage(imageUri);
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

        final int imageSize = (radius - shadowWidth - strokeWidth) * 2;
        final int r = imageSize / 2;
        if (this.icon == null) {
            Glide.with(getContext())
                    .load(imageUri)
                    .asBitmap()
                    .dontAnimate()
                    .override(imageSize, imageSize)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            BubbleView.this.icon = BitmapHelper.getRoundBitmap(resource, imageSize);
                            invalidate();
                        }
                    });
        } else if (imageSize > 0) {
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
            this.icon = null;
        }
    }

    @Override
    protected void	onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.icon = null;
    }

    public void setImage(String imageUri) {
        this.icon = null;
        this.imageUri = imageUri;
    }

    public void setImage(int resourceId) {
        final String imageUri = "android.resource://" + getContext().getPackageName() + "/" + resourceId;
        setImage(imageUri);
    }
}
