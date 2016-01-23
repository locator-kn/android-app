package com.locator_app.locator.view.fragments;


import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.locator_app.locator.R;
import com.locator_app.locator.util.CacheImageLoader;

public class ImageViewFragment extends Fragment {

    private String imageUri;

    public void setImage(final String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView imageView = new ImageView(container.getContext());
        CacheImageLoader.getInstance().setImage(imageUri, imageView);
        return imageView;
    }

}
