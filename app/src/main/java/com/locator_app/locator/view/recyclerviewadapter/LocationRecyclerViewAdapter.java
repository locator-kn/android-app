package com.locator_app.locator.view.recyclerviewadapter;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.DateConverter;
import com.locator_app.locator.view.LocationDetailActivity;
import com.locator_app.locator.view.bubble.BubbleView;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LocationRecyclerViewAdapter extends RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder> {

    List<LocatorLocation> locations = new LinkedList<>();

    public void setLocations(List<LocatorLocation> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    private int itemBackgroundColor = Color.WHITE;
    public void setItemBackgroundColor(int color) { itemBackgroundColor = color; }
    private int titleColor = Color.BLACK;
    public void setTitleColor(int color) { titleColor = color; }
    private int descrColor = Color.BLACK;
    public void setDescrColor(int color) { descrColor = color; }

    private ListItemFiller listItemFiller = (title, description, creationDate, imageView, location) -> {
        title.setText(location.title);
        description.setText(location.description);
        Glide.with(LocatorApplication.getAppContext())
                .load(location.thumbnailUri())
                .dontAnimate()
                .into(imageView);
        if (location.createDate != null) {
            String formattedDate = DateConverter.toddMMyyyy(location.createDate);
            creationDate.setText(formattedDate);
        }
    };
    public void setListItemFiller(ListItemFiller filler) {
        listItemFiller = filler;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.default_list_item, parent, false);

        view.setBackgroundColor(itemBackgroundColor);
        TextView text = (TextView) view.findViewById(R.id.text);
        TextView desc = (TextView) view.findViewById(R.id.description);

        CircleImageView civ = (CircleImageView) view.findViewById(R.id.bubbleView);
        civ.setBorderColor(Color.RED);

        text.setTextColor(titleColor);
        desc.setTextColor(descrColor);

        return new ViewHolder(view, listItemFiller);
    }

    LocationClickHandler locationClickHandler = (v, location) -> {
        Intent intent = new Intent(v.getContext(), LocationDetailActivity.class);
        intent.putExtra("location", location);
        v.getContext().startActivity(intent);
    };

    public void setLocationClickHandler(LocationClickHandler handler) {
        locationClickHandler = handler;
    }

    public interface LocationClickHandler {
        void handleLocationItemClick(View v, LocatorLocation location);
    }

    @Override
    public void onBindViewHolder(final LocationRecyclerViewAdapter.ViewHolder holder, int position) {
        final LocatorLocation location = locations.get(position);
        holder.update(location);
        holder.view.setOnClickListener( v -> locationClickHandler.handleLocationItemClick(v, location));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static abstract class GenericViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView title;
        public final TextView description;
        public final TextView creationDate;
        public final CircleImageView imageView;

        public GenericViewHolder(View view) {
            super(view);
            this.view = view;
            title = (TextView) view.findViewById(R.id.text);
            description = (TextView) view.findViewById(R.id.description);
            creationDate = (TextView) view.findViewById(R.id.bubble_info);
            imageView = (CircleImageView) view.findViewById(R.id.bubbleView);
        }
    }

    static class ViewHolder extends GenericViewHolder {
        private ListItemFiller listItemFiller;

        public ViewHolder(View view, ListItemFiller filler) {
            super(view);
            listItemFiller = filler;
        }

        public void update(LocatorLocation location) {
            listItemFiller.fillItem(title, description, creationDate, imageView, location);
        }
    }

    public interface ListItemFiller {
        void fillItem(TextView title, TextView description,
                      TextView creationDate, CircleImageView imageView,
                      LocatorLocation location);
    }
}

