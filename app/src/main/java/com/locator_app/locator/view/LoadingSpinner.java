package com.locator_app.locator.view;

import android.app.Activity;
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
    public static void showSpinner(Activity activity) {
        ImageView loadingSpinner = (ImageView) activity.findViewById(R.id.loadingSpinner);
        assert(loadingSpinner != null);

        View content = activity.findViewById(R.id.content);
        if (content != null) {
            fadeOut(activity, content);
        }

        Glide.with(activity).load(R.drawable.preloader)
                .asGif()
                .into(loadingSpinner);
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
