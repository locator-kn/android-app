package com.locator_app.locator.controller;

import android.widget.Toast;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.search.SearchApiService;
import com.locator_app.locator.apiservice.search.SearchResponse;
import com.locator_app.locator.model.LocatorLocation;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchController {
    SearchApiService searchService;

    public Observable<List<LocatorLocation>> search(String searchString,
                                                        double lon, double lat) {
        return searchService.search(searchString, lon, lat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError((exception) -> Toast.makeText(LocatorApplication.getAppContext(),
                        "Search failed",
                        Toast.LENGTH_LONG).show());
    }

    private static SearchController instance;
    public static SearchController getInstance() {
        if (instance == null) {
            instance = new SearchController();
        }
        return instance;
    }
    private SearchController() { searchService = new SearchApiService(); }
}
