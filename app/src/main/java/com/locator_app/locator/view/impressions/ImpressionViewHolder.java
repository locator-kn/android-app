package com.locator_app.locator.view.impressions;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.locator_app.locator.model.impressions.AbstractImpression;

public abstract class ImpressionViewHolder extends RecyclerView.ViewHolder {

    public ImpressionViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(AbstractImpression impression);
}
