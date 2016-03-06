package com.locator_app.locator.view.impressions;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.impressions.AbstractImpression;

class CreateImpressionViewHolder extends ImpressionViewHolder {

    TextView numberOfImpressions;

    private ImpressionRecyclerViewAdapter impressionRecyclerViewAdapter;

    public CreateImpressionViewHolder(ImpressionRecyclerViewAdapter impressionRecyclerViewAdapter, View itemView) {
        super(itemView);
        this.impressionRecyclerViewAdapter = impressionRecyclerViewAdapter;
        numberOfImpressions = (TextView)itemView.findViewById(R.id.numberOfImpressions);
        numberOfImpressions.setText("");

        ImageView image = (ImageView) itemView.findViewById(R.id.imageImpression);
        image.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ImpressionController.class);
            intent.putExtra("locationId", impressionRecyclerViewAdapter.location.id);
            intent.putExtra("type", "image");
            v.getContext().startActivity(intent);
        });

        ImageView video = (ImageView) itemView.findViewById(R.id.videoImpression);
        video.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ImpressionController.class);
            intent.putExtra("locationId", impressionRecyclerViewAdapter.location.id);
            intent.putExtra("type", "video");
            v.getContext().startActivity(intent);
        });

        ImageView text = (ImageView) itemView.findViewById(R.id.textImpression);
        text.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ImpressionController.class);
            intent.putExtra("locationId", impressionRecyclerViewAdapter.location.id);
            intent.putExtra("type", "text");
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public void bind(AbstractImpression impression) {
    }

    public void setNumberOfImpressions(int count) {
        if (count > 0) {
            numberOfImpressions.setText(String.format("%d Impressions", count));
        }
    }
}
