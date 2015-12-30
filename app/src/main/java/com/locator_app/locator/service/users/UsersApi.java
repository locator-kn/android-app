package com.locator_app.locator.service.users;

import com.locator_app.locator.service.users.LoginRequest;
import com.locator_app.locator.service.users.LoginResponse;
import com.locator_app.locator.service.users.LogoutResponse;
import com.locator_app.locator.service.users.RegistrationRequest;
import com.locator_app.locator.service.users.RegistrationResponse;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public interface UsersApi {

    @POST("/users/login")
    Observable<Response<LoginResponse>> login(@Body LoginRequest loginBodyRequest);

    @GET("/users/logout")
    Observable<Response<LogoutResponse>> logout();

    @GET("/users/protected")
    Observable<Response<Object>> requestProtected();

    @POST("/users/register")
    Observable<Response<RegistrationResponse>> register(@Body RegistrationRequest registrationBodyRequest);

}
