package com.locator_app.locator.view.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.view.ImageActivity;

public class ImageViewFragment extends Fragment {

    private String imageUri;

    public void setImage(final String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView imageView = new ImageView(container.getContext());
        Glide.with(this).load(imageUri).centerCrop().into(imageView);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(imageView.getContext(), ImageActivity.class);
            intent.putExtra("uri", imageUri);
            v.getContext().startActivity(intent);
        });
        return imageView;
    }
}
