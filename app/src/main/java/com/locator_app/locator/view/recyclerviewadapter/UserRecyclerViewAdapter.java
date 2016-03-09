package com.locator_app.locator.view.recyclerviewadapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.model.User;
import com.locator_app.locator.view.profile.ProfileActivity;

import java.util.LinkedList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    List<User> users = new LinkedList<>();

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public UserRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.default_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserRecyclerViewAdapter.ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.update(user);
        holder.view.setOnClickListener(v -> {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("profile", user);
                    context.startActivity(intent);
                }
        );
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView name;
        public final TextView description;
        public final ImageView image;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.text);
            description = (TextView) view.findViewById(R.id.description);
            description.setVisibility(View.GONE);
            name.setGravity(Gravity.CENTER_VERTICAL);
            image = (ImageView)view.findViewById(R.id.image);

            TextView bubbleInfo = (TextView) view.findViewById(R.id.bubble_info);
            bubbleInfo.setVisibility(View.INVISIBLE);
        }

        public void update(User user) {
            name.setText(user.name);
            Glide.with(LocatorApplication.getAppContext())
                    .load(user.getProfilePictureNormalSize())
                    .error(R.drawable.profile_black)
                    .bitmapTransform(new CropCircleTransformation(image.getContext()))
                    .dontAnimate()
                    .into(image);
        }
    }
}
