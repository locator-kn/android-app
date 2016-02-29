package com.locator_app.locator.apiservice.errorhandling;


import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.locator_app.locator.apiservice.errorhandling.RequestError.RequestErrorType;
import com.squareup.okhttp.MultipartBuilder;

import java.net.UnknownHostException;
import java.util.List;

import retrofit.Response;
import rx.Observable;
import rx.functions.Func1;

public class GenericErrorHandler {

    public static <T> Observable<T> wrapSingle(Observable<Response<T>> observable) {
        return observable
                .doOnError(GenericErrorHandler::logErrorMessage)
                .onErrorResumeNext(throwable -> {
                    Log.d("RequestError", throwable.getClass().toString());
                    return Observable.error(getRequestErrorFromThrowable(throwable));
                })
                .flatMap(response -> {
                    if (response.isSuccess()) {
                        return Observable.just(response.body());
                    }
                    Log.d("RequestHeaders:", response.raw().request().headers().toString());
                    Log.d("Request", response.errorBody().toString());
                    return Observable.error(getHttpErrorFromResponse(response));
                });
    }

    private static void logErrorMessage(Throwable throwable) {
        if (throwable != null) {
            Log.d("Exception occurred: ", throwable.getMessage());
        } else {
            Log.d("Exception occurred: ", "[throwable is null]");
        }
    }

    public static <T> Observable<T> wrapList(Observable<Response<List<T>>> observable) {
        return wrapSingle(observable)
                .flatMapIterable(x -> x);
    }

    private static HttpError getHttpErrorFromResponse(Response response) {
        return new HttpError(response.code());
    }

    private static RequestError getRequestErrorFromThrowable(Throwable throwable) {
        RequestErrorType errorType = RequestErrorType.Other;
        if (throwable instanceof UnknownHostException) {
            errorType = RequestErrorType.ServerUnreachable;
        } else if (throwable instanceof JsonSyntaxException) {
            errorType = RequestErrorType.InvalidJson;
        }
        return new RequestError(errorType);
    }
}
