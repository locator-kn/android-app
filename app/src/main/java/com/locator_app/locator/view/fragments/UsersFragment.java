package com.locator_app.locator.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.locator_app.locator.R;
import com.locator_app.locator.view.DividerItemDecoration;
import com.locator_app.locator.view.adapter.UserRecyclerViewAdapter;

public class UsersFragment extends Fragment {

    public final UserRecyclerViewAdapter adapter = new UserRecyclerViewAdapter();
    private RecyclerView view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = (RecyclerView) inflater.inflate(R.layout.fragment_list, container, false);
            view.setLayoutManager(new LinearLayoutManager(view.getContext()));
            view.addItemDecoration(new DividerItemDecoration(getContext(), null));
            view.setAdapter(adapter);
        }
        return view;
    }
}
