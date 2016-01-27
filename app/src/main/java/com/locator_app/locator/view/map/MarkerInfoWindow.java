package com.locator_app.locator.view.map;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.view.fragments.ImageViewFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MarkerInfoWindow extends Fragment {
    @Bind(R.id.titleTextView)
    TextView titleTextView;
    private String titleText = "";

    @Bind(R.id.nameTextView)
    TextView nameTextView;
    private String nameText = "Gott";

    @Bind(R.id.viewsTextView)
    TextView viewsTextView;
    private String viewsText = "0";

    @Bind(R.id.followersTextView)
    TextView followersTextView;
    private String followersText = "0";

    @Bind(R.id.journeysTextView)
    TextView journeysTextView;
    private String journeysText = "0";

    @Bind(R.id.titleImageView)
    ImageView titleImageView;

    @Bind(R.id.dotButton)
    Button dotButton;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_marker_info_window, container, false);
        ButterKnife.bind(this, view);

        dotButton.setVisibility(View.GONE);

        return view;
    }

    public View getView() {
        titleTextView.setText(titleText);
        nameTextView.setText(nameText);
        viewsTextView.setText(viewsText);
        followersTextView.setText(followersText);
        journeysTextView.setText(journeysText);

        return view;
    }

    public void setLocationTitle(String title) {
        titleText = title;
    }

    public void setCreatorName(String name) {
        nameText = name;
    }

    public void setViews(int views) {
        viewsText = String.valueOf(views);
    }

    public void setFollowers(int followers) {
        followersText = String.valueOf(followers);
    }

    public void setJourneys(int journeys) {
        journeysText = String.valueOf(journeys);
    }

    public void setImage(String imageUrl, Context context) {
        Glide.with(context).load(imageUrl).into(titleImageView);
    }
}
