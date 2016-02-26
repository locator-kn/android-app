package com.locator_app.locator.apiservice.search;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;
import com.locator_app.locator.apiservice.schoenhier.SchoenHiersNearbyResponse;
import com.locator_app.locator.apiservice.users.RegistrationResponse;
import com.locator_app.locator.model.LocatorLocation;

import java.net.UnknownHostException;
import java.util.List;

import retrofit.HttpException;
import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public class SearchApiService {
    public interface SearchApi {

        @GET(Api.version + "/locations/search/{searchString}")
        Observable<Response<SearchResponse>> search(@Path("searchString") String searchString,
                                                    @Query("long") double lon,
                                                    @Query("lat") double lat);
    }

    private SearchApi service = ServiceFactory.createService(SearchApi.class);

    public Observable<List<LocatorLocation>> search(String searchString,
                                             double lon,
                                             double lat) {
        return GenericErrorHandler.wrapSingle(service.search(searchString, lon, lat))
                .flatMap(response -> {
                    List<LocatorLocation> allLocations = response.locatorLocations;
                    allLocations.addAll(response.googleLocations);
                    return Observable.just(allLocations);
                });
    }
}
