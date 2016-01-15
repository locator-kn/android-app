package com.locator_app.locator.view.fragments;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.DateConverter;
import com.locator_app.locator.view.adapter.RowLayoutAdapter;

import rx.Observable;

public class LocationsFragment extends Fragment {

    RowLayoutAdapter adapter = new RowLayoutAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView listView = new ListView(LocatorApplication.getAppContext());
        listView.setAdapter(adapter);
        listView.setDivider(new ColorDrawable(0xFF000000));
        listView.setDividerHeight(1);
        return listView;
    }

    public void loadLocations(Observable<LocatorLocation> observable) {
        observable.map(this::locationToRowLayoutItem)
                .toList()
                .subscribe(
                        (list) -> adapter.setRowLayoutItems(list),
                        (error) -> Toast.makeText(LocatorApplication.getAppContext(),
                                "something went wrong :-(", Toast.LENGTH_SHORT).show()
                );
    }

    private RowLayoutAdapter.RowLayoutItem locationToRowLayoutItem(LocatorLocation location) {
        RowLayoutAdapter.RowLayoutItem item = new RowLayoutAdapter.RowLayoutItem();
        item.imageUri = location.thumbnailUri();
        item.text = location.title;
        item.description = location.description;
        item.creationDate = DateConverter.toddMMyyyy(location.createDate);
        return item;
    }
}
