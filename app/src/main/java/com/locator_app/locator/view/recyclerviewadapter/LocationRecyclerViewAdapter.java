package com.locator_app.locator.view.recyclerviewadapter;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.DateConverter;
import com.locator_app.locator.view.LocationDetailActivity;

import java.util.LinkedList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

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

        ImageView circle = (ImageView) view.findViewById(R.id.circle);
        Glide.with(view.getContext()).load(R.drawable.circle_red).into(circle);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LocatorLocation location = locations.get(position);
        holder.bind(location);
        holder.view.setOnClickListener( v -> {
            Intent intent = new Intent(v.getContext(), LocationDetailActivity.class);
            intent.putExtra("location", location);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView title;
        public final TextView description;
        public final TextView creationDate;
        public final ImageView image;
        public final ImageView circle;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = (TextView) view.findViewById(R.id.text);
            description = (TextView) view.findViewById(R.id.description);
            creationDate = (TextView) view.findViewById(R.id.bubble_info);
            image = (ImageView) view.findViewById(R.id.image);
            circle = (ImageView) view.findViewById(R.id.circle);
            Glide.with(circle.getContext())
                    .load(R.drawable.circle_silvergray)
                    .into(circle);
        }

        public void bind(LocatorLocation location) {
            title.setText(location.title);
            description.setText(location.city.title);
            creationDate.setText(DateConverter.toddMMyyyy(location.createDate));
            Glide.with(image.getContext())
                    .load(location.thumbnailUri())
                    .error(R.drawable.location_auf_map)
                    .bitmapTransform(new CropCircleTransformation(image.getContext()))
                    .into(image);
        }
    }
}

