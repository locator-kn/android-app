package com.locator_app.locator.view.recyclerviewadapter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.model.GoogleLocation;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.view.fragments.SearchResultsFragment;

import java.util.LinkedList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>{

    private List<? extends LocatorLocation> locations = new LinkedList<>();

    private final OnLocationClickListener onClickListener;

    public SearchRecyclerViewAdapter(OnLocationClickListener onLocationClickListener) {
        this.onClickListener = onLocationClickListener;
    }

    public void setLocations(List<? extends LocatorLocation> locations) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocatorLocation location = locations.get(position);
        holder.bind(location);
        holder.view.setOnClickListener(v -> onClickListener.handleClick(location));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView title;
        public final TextView description;
        public final ImageView image;
        public final ImageView circle;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            view.setBackgroundColor(Color.TRANSPARENT);
            title = (TextView) view.findViewById(R.id.text);
            title.setTextColor(Color.WHITE);
            description = (TextView) view.findViewById(R.id.description);
            description.setTextColor(Color.WHITE);
            image = (ImageView) view.findViewById(R.id.image);
            circle = (ImageView) view.findViewById(R.id.circle);
            TextView creationDate = (TextView) view.findViewById(R.id.bubble_info);
            creationDate.setText("");
        }

        public void bind(LocatorLocation location) {
            if (location instanceof SearchResultsFragment.DummyLocation) {
                hideViewElements();
            } else {
                showViewElements();
                if (location instanceof GoogleLocation) {
                    bindGoogleLocation((GoogleLocation)location);
                } else {
                    bindLocatorLocation(location);
                }
            }
        }

        private void hideViewElements() {
            title.setVisibility(View.INVISIBLE);
            description.setVisibility(View.INVISIBLE);
            circle.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
        }

        private void showViewElements() {
            title.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            circle.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
        }

        private void bindGoogleLocation(GoogleLocation location) {
            title.setText(location.title);
            description.setText("Vorschlag von Google");
            Glide.with(image.getContext())
                    .load(R.drawable.google_location)
                    .bitmapTransform(new CropCircleTransformation(image.getContext()))
                    .into(image);
        }

        private void bindLocatorLocation(LocatorLocation location) {
            title.setText(location.title);
            description.setText("Vorschlag von Locator");
            Glide.with(image.getContext())
                    .load(location.thumbnailUri())
                    .error(R.drawable.location_auf_map)
                    .bitmapTransform(new CropCircleTransformation(image.getContext()))
                    .into(image);
        }
    }

    public interface OnLocationClickListener {
        void handleClick(LocatorLocation location);
    }
}
