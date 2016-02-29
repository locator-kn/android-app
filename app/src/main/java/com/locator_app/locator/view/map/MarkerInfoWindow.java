package com.locator_app.locator.view.map;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.Marker;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.view.fragments.ImageViewFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MarkerInfoWindow extends Fragment {
    @Bind(R.id.titleTextView)
    TextView titleTextView;
    private String titleText = "";

    @Bind(R.id.nameTextView)
    TextView nameTextView;
    private String nameText = "Gott";
    private String currentUserId = "";

    @Bind(R.id.followersTextView)
    TextView followersTextView;
    private String followersText = "0";

    @Bind(R.id.titleImageView)
    ImageView titleImageView;
    private String currentImageUrl = "";

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
        followersTextView.setText(followersText);

        return view;
    }

    public void setLocationTitle(String title) {
        titleText = title;
    }

    synchronized
    public void setCreatorName(String userId, Marker marker) {
        if (!currentUserId.equals(userId)) {
            currentUserId = userId;
            UserController.getInstance().getUser(userId)
                    .subscribe(
                            (user) -> {
                                nameTextView.setText(user.name);
                                marker.showInfoWindow();
                            },
                            (error) -> {}
                    );
        }
    }

    public void setFollowers(int followers) {
        followersText = String.valueOf(followers);
    }

    synchronized
    public void setImage(String imageUrl, Context context, Marker marker) {
        if (!currentImageUrl.equals(imageUrl)) {
            currentImageUrl = imageUrl;
            titleImageView.setVisibility(View.GONE);
            Glide.with(context).load(imageUrl).asBitmap().dontAnimate()
                               .centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                    titleImageView.setImageBitmap(resource);
                    titleImageView.setVisibility(View.VISIBLE);
                    marker.showInfoWindow();
                }
            });
        }
    }
}
