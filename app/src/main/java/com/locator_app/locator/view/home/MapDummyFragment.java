package com.locator_app.locator.view.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.view.bubble.BubbleController;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapDummyFragment extends Fragment {

    @Bind(R.id.loadingSpinner)
    ImageView loadingSpinner;

    public MapDummyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_dummy, container, false);
        ButterKnife.bind(this, view);
        Glide.with(getContext()).load(R.drawable.preloader)
                .asGif()
                .into(loadingSpinner);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
