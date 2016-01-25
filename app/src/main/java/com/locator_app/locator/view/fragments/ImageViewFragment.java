package com.locator_app.locator.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;

public class ImageViewFragment extends Fragment {

    private String imageUri;

    public void setImage(final String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView imageView = new ImageView(container.getContext());
        Glide.with(this).load(imageUri).centerCrop().crossFade(2000).into(imageView);
        return imageView;
    }
}
