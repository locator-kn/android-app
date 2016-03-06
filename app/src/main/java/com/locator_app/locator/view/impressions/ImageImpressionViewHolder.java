package com.locator_app.locator.view.impressions;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.ImageImpression;
import com.locator_app.locator.util.DateConverter;
import com.locator_app.locator.view.ImageActivity;
import com.locator_app.locator.view.profile.ProfileActivity;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ImageImpressionViewHolder extends ImpressionViewHolder {

    ImageView userImage;
    TextView date;
    TextView userName;
    ImageView impressionImage;

    public ImageImpressionViewHolder(View itemView) {
        super(itemView);
        userImage = (ImageView) itemView.findViewById(R.id.userThumbnail);
        date = (TextView) itemView.findViewById(R.id.date);
        userName = (TextView) itemView.findViewById(R.id.userName);
        impressionImage = (ImageView) itemView.findViewById(R.id.impressionImage);

        ImageView impressionType = (ImageView) itemView.findViewById(R.id.impressionType);
        Glide.with(itemView.getContext()).load(R.drawable.small_gray_photo).into(impressionType);
    }

    @Override
    public void bind(AbstractImpression impression) {
        ImageImpression imageImpression = (ImageImpression) impression;
        UserController.getInstance().getUser(imageImpression.getUserId())
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
                        (error) -> {}
                );
        date.setText(DateConverter.toddMMyyyy(imageImpression.getCreateDate()));
        Glide.with(itemView.getContext())
                .load(imageImpression.getImageUri())
                .centerCrop()
                .dontAnimate()
                .into(impressionImage);

        impressionImage.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ImageActivity.class);
            intent.putExtra("uri", imageImpression.getImageUri());
            v.getContext().startActivity(intent);
        });
    }
}
