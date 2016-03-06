package com.locator_app.locator.view.impressions;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.VideoImpression;
import com.locator_app.locator.util.DateConverter;
import com.locator_app.locator.view.profile.ProfileActivity;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class VideoImpressionViewHolder extends ImpressionViewHolder {

    ImageView userImage;
    TextView date;
    TextView userName;
    ImageView videoPreview;

    public VideoImpressionViewHolder(View itemView) {
        super(itemView);
        ImageView impressionType = (ImageView) itemView.findViewById(R.id.impressionType);
        Glide.with(itemView.getContext()).load(R.drawable.small_gray_video).into(impressionType);

        userImage = (ImageView) itemView.findViewById(R.id.userThumbnail);
        date = (TextView) itemView.findViewById(R.id.date);
        userName = (TextView) itemView.findViewById(R.id.userName);
        videoPreview = (ImageView) itemView.findViewById(R.id.videoPreview);
    }

    @Override
    public void bind(AbstractImpression impression) {
        VideoImpression videoImpression = (VideoImpression) impression;

        UserController.getInstance().getUser(videoImpression.getUserId())
                .subscribe(
                        (user) -> {
                            userName.setText(user.name);
                            Glide.with(userImage.getContext())
                                    .load(user.thumbnailUri())
                                    .error(R.drawable.profile_black)
                                    .bitmapTransform(new CropCircleTransformation(userImage.getContext()))
                                    .dontAnimate()
                                    .into(userImage);
                            userImage.setOnClickListener(v -> {
                                        Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                                        intent.putExtra("profile", user);
                                        v.getContext().startActivity(intent);
                                    }
                            );
                        },
                        (err) -> { }
                );
        date.setText(DateConverter.toddMMyyyy(videoImpression.getCreateDate()));

        videoPreview.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(videoImpression.getVideoUri()), "video/*");
            v.getContext().startActivity(intent);
        });
    }
}
