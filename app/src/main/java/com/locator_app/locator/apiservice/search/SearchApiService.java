package com.locator_app.locator.apiservice.search;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;
import com.locator_app.locator.apiservice.locations.LocationsNearbyResponse;
import com.locator_app.locator.model.LocatorLocation;

import java.util.List;
import java.util.Vector;

import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public class SearchApiService {
    public interface SearchApi {
        @GET(Api.version + "/locations/search")
        Observable<Response<SearchResponse>> searchString(@Query("long") double lon,
                                                          @Query("lat") double lat,
                                                          @Query("locationName") String searchString);

        @GET(Api.version + "/locations/search")
        Observable<Response<SearchResponse>> search(@Query("long") double lon,
                                                    @Query("lat") double lat);
    }

    private SearchApi service = ServiceFactory.createService(SearchApi.class);

    public Observable<List<LocatorLocation>> searchString(String searchString,
                                                          double lon,
                                                          double lat) {
        return GenericErrorHandler.wrapSingle(service.searchString(lon, lat, searchString))
                .flatMap(this::parseResponseToList);
    }

    public Observable<List<LocatorLocation>> search(double lon,
                                                    double lat) {
        return GenericErrorHandler.wrapSingle(service.search(lon, lat))
                .flatMap(this::parseResponseToList);
    }

    private Observable<List<LocatorLocation>> parseResponseToList(SearchResponse response) {
        List<LocatorLocation> allLocations = new Vector<>();
        for (LocationsNearbyResponse.Result locationResult : response.locatorLocations) {
            allLocations.add(locationResult.location);
        }
        allLocations.addAll(response.googleLocations);
        return Observable.just(allLocations);
    }
}
