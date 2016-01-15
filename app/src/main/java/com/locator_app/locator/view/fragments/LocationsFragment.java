package com.locator_app.locator.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.view.adapter.RowLayoutAdapter;

import java.util.LinkedList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        List<RowLayoutAdapter.RowLayoutItem> rowLayoutItems = new LinkedList<>();
        LocationController.getInstance().getLocationsNearby(9.169753789901733, 47.66868204997508, 2, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::locationToRowLayoutItem)
                .subscribe(
                        (item) -> rowLayoutItems.add(item),
                        (error) -> Toast.makeText(LocatorApplication.getAppContext(),
                                "something went wrong :-(", Toast.LENGTH_SHORT).show(),
                        () -> adapter.setRowLayoutItems(rowLayoutItems)
                );
    }

    private RowLayoutAdapter.RowLayoutItem locationToRowLayoutItem(LocatorLocation location) {
        RowLayoutAdapter.RowLayoutItem item = new RowLayoutAdapter.RowLayoutItem();
        item.imageUri = location.thumbnailUri();
        item.text = location.title;
        item.description = location.description;
        return item;
    }
}
