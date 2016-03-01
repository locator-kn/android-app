package com.locator_app.locator.view.impressions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.locator_app.locator.R;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.AbstractImpression.ImpressionType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ImpressionRecyclerViewAdapter
        extends RecyclerView.Adapter<ImpressionViewHolder>{

    final int locationInformationViewType = 100;
    final int createNewImpressionViewType = 200;
    final int numberOfAdditionalInfoTypes = 2;

    List<AbstractImpression> impressions = new LinkedList<>();
    public LocatorLocation location = null;
    LocationInfoViewHolder locationInfo = null;
    CreateImpressionViewHolder createImpressionViewHolder = null;
    final List<AbstractImpression.ImpressionType> supportedImpressionTypes =
            Arrays.asList(ImpressionType.IMAGE, ImpressionType.VIDEO, ImpressionType.TEXT);

    public void setLocation(LocatorLocation location) {
        this.location = location;
        notifyDataSetChanged();
    }

    public void setImpressions(List<AbstractImpression> impressions) {
        this.impressions = impressions;
        if (createImpressionViewHolder != null) {
            createImpressionViewHolder.setNumberOfImpressions(impressions.size());
        }
        notifyDataSetChanged();
    }

    public void updateFavorCounter() {
        locationInfo.updateFavorCounter();
        notifyDataSetChanged();
    }

    @Override
    public ImpressionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == createNewImpressionViewType) {
            if (createImpressionViewHolder == null) {
                final int cardId = R.layout.card_new_impression;
                View v = LayoutInflater.from(parent.getContext()).inflate(cardId, parent, false);
                createImpressionViewHolder = new CreateImpressionViewHolder(this, v);
            }
            return createImpressionViewHolder;
        } else if (viewType == locationInformationViewType) {
            if (locationInfo == null) {
                final int cardId = R.layout.card_location_information;
                View v = LayoutInflater.from(parent.getContext()).inflate(cardId, parent, false);
                locationInfo = new LocationInfoViewHolder(this, v);
            }
            return locationInfo;
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
    public void onBindViewHolder(ImpressionViewHolder holder, int position) {
        if (position >= numberOfAdditionalInfoTypes) {
            holder.bind(impressions.get(position - numberOfAdditionalInfoTypes));
        }
    }

    @Override
    public int getItemCount() {
        return impressions.size() + numberOfAdditionalInfoTypes;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) { // show location information card
            return locationInformationViewType;
        } else if (position == 1) { // create new impression card
            return createNewImpressionViewType;
        }
        ImpressionType type = impressions.get(position - numberOfAdditionalInfoTypes).type();
        return supportedImpressionTypes.indexOf(type);
    }
}
