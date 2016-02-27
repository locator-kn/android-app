package com.locator_app.locator.view;

import android.app.Activity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;

public class LoadingSpinner {
    public static void showSpinner(Activity activity) {
        ImageView loadingSpinner = (ImageView) activity.findViewById(R.id.loadingSpinner);
        assert(loadingSpinner != null);

        View content = activity.findViewById(R.id.content);
        if (content != null) {
            fadeOut(content);
        }

        Glide.with(activity).load(R.drawable.preloader).into(loadingSpinner);
    }

    private static void fadeOut(View v) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);

        v.setAnimation(fadeOut);
    }
}
