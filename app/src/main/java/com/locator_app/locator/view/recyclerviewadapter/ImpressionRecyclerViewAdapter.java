package com.locator_app.locator.view.recyclerviewadapter;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.AbstractImpression.ImpressionType;
import com.locator_app.locator.model.impressions.ImageImpression;
import com.locator_app.locator.model.impressions.TextImpression;
import com.locator_app.locator.model.impressions.VideoImpression;
import com.locator_app.locator.util.DateConverter;
import com.locator_app.locator.view.profile.ProfileActivity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImpressionRecyclerViewAdapter
        extends RecyclerView.Adapter<ImpressionRecyclerViewAdapter.ViewHolder>{

    final int locationDescriptionViewType = 100;
    final int createNewImpressionViewType = 200;

    List<AbstractImpression> impressions = new LinkedList<>();
    LocatorLocation location = null;
    final List<AbstractImpression.ImpressionType> supportedImpressionTypes =
            Arrays.asList(ImpressionType.IMAGE, ImpressionType.VIDEO, ImpressionType.TEXT);

    public void setLocation(LocatorLocation location) {
        this.location = location;
        notifyDataSetChanged();
    }

    public void setImpressions(List<AbstractImpression> impressions) {
        this.impressions = impressions;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == createNewImpressionViewType) {
            final int cardId = R.layout.card_new_impression;
            View v = LayoutInflater.from(parent.getContext()).inflate(cardId, parent, false);
            return new CreateImpressionViewHolder(v);
        } else if (viewType == locationDescriptionViewType) {
            final int cardId = R.layout.card_location_description;
            View v = LayoutInflater.from(parent.getContext()).inflate(cardId, parent, false);
            return new LocationDescriptionViewHolder(v);
        } else {
            ImpressionType type = supportedImpressionTypes.get(viewType);
            if (type == ImpressionType.IMAGE) {
                final int cardId = R.layout.card_impression_image;
                View v = LayoutInflater.from(parent.getContext()).inflate(cardId, parent, false);
                return new ImageImpressionViewHolder(v);
            } else if (type == ImpressionType.TEXT) {
                final int cardId = R.layout.card_impression_text;
                View v = LayoutInflater.from(parent.getContext()).inflate(cardId, parent, false);
                return new TextImpressionViewHolder(v);
            } else if (type == ImpressionType.VIDEO) {
                final int cardId = R.layout.card_impression_video;
                View v = LayoutInflater.from(parent.getContext()).inflate(cardId, parent, false);
                return new VideoImpressionViewHolder(v);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= 2) {
            holder.bind(impressions.get(position - 2));
        }
    }

    @Override
    public int getItemCount() {
        return impressions.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) { // show location description card
            return locationDescriptionViewType;
        } else if (position == 1) { // create new impression card
            return createNewImpressionViewType;
        }
        ImpressionType type = impressions.get(position - 2).type();
        return supportedImpressionTypes.indexOf(type);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
        public abstract void bind(AbstractImpression impression);
    }

    public class ImageImpressionViewHolder extends ViewHolder {

        ImageView userImage;
        TextView date;
        TextView userName;
        ImageView impressionImage;

        public ImageImpressionViewHolder(View itemView) {
            super(itemView);
            userImage = (ImageView)itemView.findViewById(R.id.userThumbnail);
            date = (TextView)itemView.findViewById(R.id.date);
            userName = (TextView)itemView.findViewById(R.id.userName);
            impressionImage = (ImageView)itemView.findViewById(R.id.impressionImage);

            ImageView impressionType = (ImageView)itemView.findViewById(R.id.impressionType);
            Glide.with(itemView.getContext()).load(R.drawable.small_gray_photo).into(impressionType);
        }

        @Override
        public void bind(AbstractImpression impression) {
            ImageImpression imageImpression = (ImageImpression) impression;
            UserController.getInstance().getUser(imageImpression.getUserId())
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
                            },
                            (error) -> {
                                Toast.makeText(itemView.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    );
            date.setText(DateConverter.toddMMyyyy(imageImpression.getCreateDate()));
            Glide.with(itemView.getContext())
                    .load(imageImpression.getImageUri())
                    .centerCrop()
                    .dontAnimate()
                    .into(impressionImage);
        }
    }

    public class TextImpressionViewHolder extends ViewHolder {

        ImageView userImage;
        TextView date;
        TextView userName;
        TextView impressionText;

        public TextImpressionViewHolder(View itemView) {
            super(itemView);
            userImage = (ImageView)itemView.findViewById(R.id.userThumbnail);
            date = (TextView)itemView.findViewById(R.id.date);
            userName = (TextView)itemView.findViewById(R.id.userName);
            impressionText = (TextView)itemView.findViewById(R.id.impressionText);

            ImageView impressionType = (ImageView)itemView.findViewById(R.id.impressionType);
            Glide.with(itemView.getContext()).load(R.drawable.small_gray_chat).into(impressionType);
        }

        @Override
        public void bind(AbstractImpression impression) {
            TextImpression textImpression = (TextImpression) impression;
            UserController.getInstance().getUser(textImpression.getUserId())
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
            date.setText(DateConverter.toddMMyyyy(textImpression.getCreateDate()));
            impressionText.setText(textImpression.getText());
        }
    }

    class VideoImpressionViewHolder extends ViewHolder {

        ImageView userImage;
        TextView date;
        TextView userName;
        VideoView videoView;

        public VideoImpressionViewHolder(View itemView) {
            super(itemView);
            ImageView impressionType = (ImageView)itemView.findViewById(R.id.impressionType);
            Glide.with(itemView.getContext()).load(R.drawable.small_gray_video).into(impressionType);

            userImage = (ImageView)itemView.findViewById(R.id.userThumbnail);
            date = (TextView)itemView.findViewById(R.id.date);
            userName = (TextView)itemView.findViewById(R.id.userName);

            videoView = (VideoView) itemView.findViewById(R.id.videoView);
            MediaController mc = new MediaController(itemView.getContext());
            mc.setMediaPlayer(videoView);
            videoView.setMediaController(mc);
        }

        @Override
        public void bind(AbstractImpression impression) {
            VideoImpression videoImpression = (VideoImpression)impression;
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

    class LocationDescriptionViewHolder extends ViewHolder {

        public LocationDescriptionViewHolder(View itemView) {
            super(itemView);
            TextView description = (TextView)itemView.findViewById(R.id.description);
            description.setText(location.description);
            description.setVisibility(View.GONE);
            itemView.setOnClickListener(v -> {
                if (description.getVisibility() == View.GONE) {
                    description.setVisibility(View.VISIBLE);
                } else {
                    description.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void bind(AbstractImpression impression) {
        }
    }

    class CreateImpressionViewHolder extends ViewHolder {

        public CreateImpressionViewHolder(View itemView) {
            super(itemView);
            TextView numberOfImpressions = (TextView)itemView.findViewById(R.id.numberOfImpressions);
            numberOfImpressions.setText(Integer.toString(impressions.size()) + " Impressions");
            LinearLayout impressionTypes = (LinearLayout)itemView.findViewById(R.id.impressionTypes);
            impressionTypes.setVisibility(View.GONE);
            LinearLayout showHideImpressionTypes = (LinearLayout)itemView.findViewById(R.id.showHideImpressionTypes);
            showHideImpressionTypes.setOnClickListener(v -> {
                if (impressionTypes.getVisibility() == View.GONE) {
                    impressionTypes.setVisibility(View.VISIBLE);
                } else {
                    impressionTypes.setVisibility(View.GONE);
                }
            });
            ImageView voice = (ImageView)itemView.findViewById(R.id.voiceImpression);
            voice.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "voice", Toast.LENGTH_SHORT).show());
            ImageView media = (ImageView)itemView.findViewById(R.id.mediaImpression);
            media.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "photo", Toast.LENGTH_SHORT).show());
            media.setOnLongClickListener(v -> {
                Toast.makeText(itemView.getContext(), "video", Toast.LENGTH_SHORT).show();
                return true;
            });
            ImageView text = (ImageView)itemView.findViewById(R.id.textImpression);
            text.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "text", Toast.LENGTH_SHORT).show());
        }

        @Override
        public void bind(AbstractImpression impression) {
        }
    }
}
