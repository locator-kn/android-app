package com.locator_app.locator.service;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public interface UsersApi {

    @POST("/users/login")
    Observable<Response<LoginResponse>> login(@Body LoginRequest loginRequestBody);

    @GET("/users/logout")
    Observable<Response<LogoutResponse>> logout();

    @GET("/users/protected")
    Observable<Response<Object>> requestProtected();

    @POST("/users/register")
    Observable<Response<RegistrationResponse>> register(@Body RegistrationRequest registrationBodyRequest);

}
