package com.locator_app.locator.service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class ServiceFactory {

    private static Retrofit retrofit;

    public static <T> T createService(final Class<T> className) {
        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.interceptors().add(interceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://locator-app.com")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit.create(className);
    }

    public static String apiVersion = "/api/v2";
}s