package com.locator_app.locator.view.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.DateConverter;
import com.locator_app.locator.view.bubble.BubbleView;

import java.util.LinkedList;
import java.util.List;

public class LocationRecyclerViewAdapter extends RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder> {

    List<LocatorLocation> locations = new LinkedList<>();

    public void setLocations(List<LocatorLocation> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.default_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocationRecyclerViewAdapter.ViewHolder holder, int position) {
        final LocatorLocation location = locations.get(position);
        holder.update(location);
        holder.view.setOnClickListener(v ->
                Toast.makeText(v.getContext(), location.city.title, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView title;
        public final TextView description;
        public final TextView creationDate;
        public final BubbleView bubbleView;
        private String formattedDate = "";

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = (TextView) view.findViewById(R.id.text);
            description = (TextView) view.findViewById(R.id.description);
            creationDate = (TextView) view.findViewById(R.id.bubble_info);
            bubbleView = (BubbleView) view.findViewById(R.id.bubbleView);
        }

        public void update(LocatorLocation location) {
            title.setText(location.title);
            description.setText(location.description);
            bubbleView.loadImage(location.thumbnailUri());
            if (formattedDate.isEmpty()) {
                formattedDate = DateConverter.toddMMyyyy(location.createDate);
            }
            creationDate.setText(formattedDate);
        }
    }
}

