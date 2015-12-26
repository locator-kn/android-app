package com.locator_app.locator.service;

import com.squareup.okhttp.Response;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface UsersApi {

    @POST("/users/register")
    Observable<Response> register(@Body RegistrationRequest registrationBodyRequest);

    @POST("/users/login")
    Observable<Response> login(@Body LoginRequest loginBodyRequest);
}
