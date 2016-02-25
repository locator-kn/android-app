package com.locator_app.locator.apiservice.search;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.schoenhier.SchoenHierRequest;
import com.locator_app.locator.apiservice.schoenhier.SchoenHiersNearbyResponse;
import com.locator_app.locator.apiservice.schoenhier.SchoenHiersResponse;

import java.net.UnknownHostException;

import retrofit.HttpException;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public class SearchApiService {
    public interface SearchApi {

        @GET(Api.version + "/locations/search/{searchString}")
        Observable<SearchResponse> search(@Path("searchString") String searchString,
                                          @Query("long") double lon,
                                          @Query("lat") double lat);
    }

    private SearchApi service = ServiceFactory.createService(SearchApi.class);

    public Observable<SearchResponse> search(String searchString,
                                             double lon,
                                             double lat) {
        return service.search(searchString, lon, lat);
//                .doOnError(this::handleError)
//                .flatMap(this::parseSchoenHiersNearbyResponse);
    }

    private Observable<SchoenHiersNearbyResponse> parseSearchResponse(Response response) {
        if (response.isSuccess()) {
            return Observable.just((SchoenHiersNearbyResponse) response.body());
        }
        return Observable.error(new Exception("http-error: " + Integer.toString(response.code())));
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException ex = (HttpException) throwable;
        } else if (throwable instanceof UnknownHostException) {
            UnknownHostException ex = (UnknownHostException) throwable;
        }
    }
}
