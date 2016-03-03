package com.locator_app.locator.apiservice;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class ServiceFactory {

    private static Retrofit retrofit;

    public static <T> T createService(final Class<T> className) {
        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(5, TimeUnit.MINUTES);
            client.setReadTimeout(5, TimeUnit.MINUTES);
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            client.interceptors().add(interceptor);

            CookieStore cookieStore = new PersistentCookieStore();
            CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);

            retrofit = new Retrofit.Builder()
                    .baseUrl(Api.serverUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit.create(className);
    }
}

