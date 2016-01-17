package com.locator_app.locator.view.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.locator_app.locator.model.User;

import java.util.LinkedList;
import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder> {

    List<User> users = new LinkedList<>();

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public LocationRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(LocationRecyclerViewAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
