package com.locator_app.locator.view;

import android.app.Activity;
import android.media.Image;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.locator_app.locator.R;

public class LoadingSpinner {

    Activity activity;
    ImageView loadingSpinner;
    View content;

    public LoadingSpinner(Activity a) {
        activity = a;
        loadingSpinner = (ImageView) activity.findViewById(R.id.loadingSpinner);
        content = activity.findViewById(R.id.content);

        hideSpinner();
        Glide.with(activity).load(R.drawable.preloader)
                .asGif()
                .into(loadingSpinner);
    }

    public void showSpinner() {
        if (content != null) {
            fadeOut(activity, content);
        }
        loadingSpinner.setVisibility(View.VISIBLE);
    }

    public void hideSpinner() {
        if (content != null) {
            content.setVisibility(View.VISIBLE);
        }
        loadingSpinner.setVisibility(View.INVISIBLE);
    }

    private static void fadeOut(Activity activity, View v) {
        // go to right
//        Animation out = AnimationUtils.makeOutAnimation(activity, true);
//        v.startAnimation(out);
//        v.setVisibility(View.INVISIBLE);
        Animation animationFadeOut = AnimationUtils.loadAnimation(activity, R.anim.fadeout);
        v.startAnimation(animationFadeOut);
        v.setVisibility(View.INVISIBLE);
    }
}
