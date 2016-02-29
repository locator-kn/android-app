package com.locator_app.locator.view.impressions;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.TextImpression;
import com.locator_app.locator.util.DateConverter;
import com.locator_app.locator.view.profile.ProfileActivity;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TextImpressionViewHolder extends ImpressionViewHolder {

    ImageView userImage;
    TextView date;
    TextView userName;
    TextView impressionText;

    public TextImpressionViewHolder(View itemView) {
        super(itemView);
        userImage = (ImageView) itemView.findViewById(R.id.userThumbnail);
        date = (TextView) itemView.findViewById(R.id.date);
        userName = (TextView) itemView.findViewById(R.id.userName);
        impressionText = (TextView) itemView.findViewById(R.id.impressionText);

        ImageView impressionType = (ImageView) itemView.findViewById(R.id.impressionType);
        Glide.with(itemView.getContext()).load(R.drawable.small_gray_chat).into(impressionType);
    }

    @Override
    public void bind(AbstractImpression impression) {
        TextImpression textImpression = (TextImpression) impression;
        UserController.getInstance().getUser(textImpression.getUserId())
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
                        },
                        (err) -> {}

                );
        date.setText(DateConverter.toddMMyyyy(textImpression.getCreateDate()));
        impressionText.setText(textImpression.getText());
    }
}
