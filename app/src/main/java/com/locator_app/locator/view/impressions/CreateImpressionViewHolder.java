package com.locator_app.locator.view.impressions;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.impressions.AbstractImpression;

import butterknife.Bind;
import butterknife.ButterKnife;

class CreateImpressionViewHolder extends ImpressionViewHolder {

    TextView numberOfImpressions;

    private ImpressionRecyclerViewAdapter impressionRecyclerViewAdapter;

    public CreateImpressionViewHolder(ImpressionRecyclerViewAdapter impressionRecyclerViewAdapter, View itemView) {
        super(itemView);
        this.impressionRecyclerViewAdapter = impressionRecyclerViewAdapter;
        numberOfImpressions = (TextView)itemView.findViewById(R.id.numberOfImpressions);
        numberOfImpressions.setText("");
        LinearLayout impressionTypes = (LinearLayout) itemView.findViewById(R.id.impressionTypes);
        impressionTypes.setVisibility(View.GONE);
        LinearLayout showHideImpressionTypes = (LinearLayout) itemView.findViewById(R.id.showHideImpressionTypes);
        showHideImpressionTypes.setOnClickListener(v -> {
            if (impressionTypes.getVisibility() == View.GONE) {
                impressionTypes.setVisibility(View.VISIBLE);
            } else {
                impressionTypes.setVisibility(View.GONE);
            }
        });
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
        text.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "text", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void bind(AbstractImpression impression) {
    }

    public void setNumberOfImpressions(int count) {
        if (numberOfImpressions != null && count > 0) {
            numberOfImpressions.setText(Integer.toString(count) + " Impressions");
        }
    }
}
