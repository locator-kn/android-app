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

import java.util.LinkedList;
import java.util.List;

public class SearchResultsFragment extends Fragment {

    private SearchInteractionListener listener;
    private LocationRecyclerViewAdapter adapter = new LocationRecyclerViewAdapter();
    private RecyclerView view;

    public SearchResultsFragment() {
        adapter.setItemBackgroundColor(Color.TRANSPARENT);
        adapter.setTitleColor(Color.WHITE);
        adapter.setDescrColor(Color.WHITE);

        adapter.setLocationClickHandler((v, location) -> {
            if (listener != null) {
                listener.onLocationClicked(location);
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

    public void search(double lon, double lat) {
        SearchController.getInstance().search(lon, lat)
                .subscribe(this::onSearchSuccess,
                           this::onSearchError);
    }

    public void searchString(String searchString, double lon, double lat) {
        SearchController.getInstance().searchString(searchString, lon, lat)
                .subscribe(this::onSearchSuccess,
                           this::onSearchError);
    }

    private void onSearchError(Throwable throwable) {
        listener.onSearchResult(new LinkedList<>());
    }

    private void onSearchSuccess(List<LocatorLocation> result) {
        listener.onSearchResult(result);
        adapter.setLocations(result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchInteractionListener) {
            listener = (SearchInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface SearchInteractionListener {
        void onLocationClicked(LocatorLocation location);
        void onSearchResult(List<LocatorLocation> searchResult);
    }
}
