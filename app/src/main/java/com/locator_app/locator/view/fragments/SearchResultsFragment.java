package com.locator_app.locator.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.SearchController;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.view.DividerItemDecoration;
import com.locator_app.locator.view.recyclerviewadapter.LocationRecyclerViewAdapter;

public class SearchResultsFragment extends Fragment {

    private OnSearchItemClickListener locationClickListener;
    private LocationRecyclerViewAdapter adapter = new LocationRecyclerViewAdapter();
    private RecyclerView view;

    public SearchResultsFragment() {
        adapter.setItemBackgroundColor(Color.TRANSPARENT);
        adapter.setTitleColor(Color.WHITE);
        adapter.setDescrColor(Color.WHITE);

        adapter.setLocationClickHandler((v, location) -> {
            if (locationClickListener != null) {
                locationClickListener.onLocationClicked(location);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), null);
            divider.setDividerColor(Color.LTGRAY);

            view = (RecyclerView) inflater.inflate(R.layout.fragment_list, container, false);
            view.setLayoutManager(new LinearLayoutManager(view.getContext()));
            view.addItemDecoration(divider);
            view.setHasFixedSize(true);
            view.setAdapter(adapter);
        }
        return view;
    }


    public void search(String searchString, double lon, double lat) {
        SearchController.getInstance().search(searchString, lon, lat)
                .subscribe(adapter::setLocations);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchItemClickListener) {
            locationClickListener = (OnSearchItemClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        locationClickListener = null;
    }

    public interface OnSearchItemClickListener {
        void onLocationClicked(LocatorLocation location);
    }
}
