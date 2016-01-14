package com.locator_app.locator.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.view.adapter.RowLayoutAdapter;

import rx.Observable;

public class LocationsFragment extends Fragment {

    RowLayoutAdapter adapter = new RowLayoutAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (adapter.isEmpty()) {
            loadAdapterItems();
        }
        ListView listView = new ListView(LocatorApplication.getAppContext());
        listView.setAdapter(adapter);
        return listView;
    }

    private void loadAdapterItems() {

    }
}
