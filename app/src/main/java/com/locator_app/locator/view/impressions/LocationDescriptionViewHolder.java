package com.locator_app.locator.view.impressions;

import android.view.View;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.model.impressions.AbstractImpression;

class LocationDescriptionViewHolder extends ImpressionViewHolder {

    private ImpressionRecyclerViewAdapter impressionRecyclerViewAdapter;

    public LocationDescriptionViewHolder(ImpressionRecyclerViewAdapter impressionRecyclerViewAdapter, View itemView) {
        super(itemView);
        this.impressionRecyclerViewAdapter = impressionRecyclerViewAdapter;
        TextView description = (TextView) itemView.findViewById(R.id.description);
        description.setText(impressionRecyclerViewAdapter.location.description);
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
