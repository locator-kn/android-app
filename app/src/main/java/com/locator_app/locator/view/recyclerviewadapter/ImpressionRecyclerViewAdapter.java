package com.locator_app.locator.view.recyclerviewadapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.AbstractImpression.ImpressionType;
import com.locator_app.locator.model.impressions.ImageImpression;
import com.locator_app.locator.model.impressions.TextImpression;
import com.locator_app.locator.util.DateConverter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImpressionRecyclerViewAdapter
        extends RecyclerView.Adapter<ImpressionRecyclerViewAdapter.ViewHolder>{

    List<AbstractImpression> impressions = new LinkedList<>();
    final List<AbstractImpression.ImpressionType> supportedImpressionTypes =
            Arrays.asList(ImpressionType.IMAGE, ImpressionType.VIDEO, ImpressionType.TEXT);

    public void setImpressions(List<AbstractImpression> impressions) {
        this.impressions = impressions;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(impressions.get(position));
    }

    @Override
    public int getItemCount() {
        return impressions.size();
    }

    @Override
    public int getItemViewType(int position) {
        ImpressionType type = impressions.get(position).type();
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
                                Glide.with(userImage.getContext()).load(user.thumbnailUri())
                                        .into(userImage);
                            },
                            (error) -> {
                                Toast.makeText(itemView.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    );
            date.setText(DateConverter.toddMMyyyy(imageImpression.getCreateDate()));
            Glide.with(itemView.getContext()).load(imageImpression.getImageUri()).into(impressionImage);
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
                                Glide.with(userImage.getContext()).load(user.thumbnailUri())
                                        .into(userImage);
                            }
                    );
            date.setText(DateConverter.toddMMyyyy(textImpression.getCreateDate()));
            impressionText.setText(textImpression.getText());
        }
    }
}
