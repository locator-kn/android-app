package com.locator_app.locator.view.impressions;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.impressions.AbstractImpression;

class CreateImpressionViewHolder extends ImpressionViewHolder {

    private ImpressionRecyclerViewAdapter impressionRecyclerViewAdapter;

    public CreateImpressionViewHolder(ImpressionRecyclerViewAdapter impressionRecyclerViewAdapter, View itemView) {
        super(itemView);
        this.impressionRecyclerViewAdapter = impressionRecyclerViewAdapter;
        TextView numberOfImpressions = (TextView) itemView.findViewById(R.id.numberOfImpressions);
        numberOfImpressions.setText(Integer.toString(impressionRecyclerViewAdapter.impressions.size()) + " Impressions");
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
        ImageView voice = (ImageView) itemView.findViewById(R.id.voiceImpression);
        voice.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "voice", Toast.LENGTH_SHORT).show());
        ImageView media = (ImageView) itemView.findViewById(R.id.mediaImpression);
        media.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "photo", Toast.LENGTH_SHORT).show());
        media.setOnLongClickListener(v -> {
            Toast.makeText(itemView.getContext(), "video", Toast.LENGTH_SHORT).show();
            return true;
        });
        ImageView text = (ImageView) itemView.findViewById(R.id.textImpression);
        text.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "text", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void bind(AbstractImpression impression) {
    }
}
