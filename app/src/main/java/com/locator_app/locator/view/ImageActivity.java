package com.locator_app.locator.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.locator_app.locator.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageActivity extends Activity {

    @Bind(R.id.imageView)
    SubsamplingScaleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("uri")) {
            String uri = getIntent().getStringExtra("uri");
            Glide.with(getApplicationContext())
                    .load(uri)
                    .asBitmap()
                    .dontAnimate()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            imageView.setImage(ImageSource.bitmap(resource));
                        }
                    });
        } else {
            Log.d("ImageActivity", "no such uri");
            finish();
        }
    }
}
