package com.locator_app.locator.service;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class ServiceFactory {

    private static Retrofit retrofit;

    public static <T> T createService(final Class<T> className) {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://locator-app.com/api/v2")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit.create(className);
    }
}
