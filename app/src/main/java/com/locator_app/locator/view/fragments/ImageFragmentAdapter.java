package com.locator_app.locator.view.fragments;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.locator_app.locator.R;

import java.util.LinkedList;
import java.util.List;

public class ImageFragmentAdapter extends FragmentStatePagerAdapter {

    private List<String> imageUris = new LinkedList<>();

    public ImageFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setImages(final List<String> imageUris) {
        this.imageUris = imageUris;
    }

    @Override
    public Fragment getItem(int position) {
        ImageViewFragment fragment = new ImageViewFragment();
        fragment.setImage(imageUris.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }
}
