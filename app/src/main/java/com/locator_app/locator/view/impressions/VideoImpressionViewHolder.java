package com.locator_app.locator.view.impressions;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.VideoImpression;
import com.locator_app.locator.util.DateConverter;
import com.locator_app.locator.view.profile.ProfileActivity;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class VideoImpressionViewHolder extends ImpressionViewHolder {

    ImageView userImage;
    TextView date;
    TextView userName;
    VideoView videoView;

    public VideoImpressionViewHolder(View itemView) {
        super(itemView);
        ImageView impressionType = (ImageView) itemView.findViewById(R.id.impressionType);
        Glide.with(itemView.getContext()).load(R.drawable.small_gray_video).into(impressionType);

        userImage = (ImageView) itemView.findViewById(R.id.userThumbnail);
        date = (TextView) itemView.findViewById(R.id.date);
        userName = (TextView) itemView.findViewById(R.id.userName);

        videoView = (VideoView) itemView.findViewById(R.id.videoView);
        MediaController mc = new MediaController(itemView.getContext());
        mc.setMediaPlayer(videoView);
        videoView.setMediaController(mc);
    }

    @Override
    public void bind(AbstractImpression impression) {
        VideoImpression videoImpression = (VideoImpression) impression;
        Uri uri = Uri.parse(videoImpression.getVideoUri());
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.pause();

        UserController.getInstance().getUser(videoImpression.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (user) -> {
                            userName.setText(user.name);
                            Glide.with(userImage.getContext())
                                    .load(user.thumbnailUri())
                                    .error(R.drawable.profile_black)
                                    .dontAnimate()
                                    .into(userImage);
                            userImage.setOnClickListener(v -> {
                                        Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                                        intent.putExtra("profile", user);
                                        v.getContext().startActivity(intent);
                                    }
                            );
                        }
                );
        date.setText(DateConverter.toddMMyyyy(videoImpression.getCreateDate()));
    }
}
