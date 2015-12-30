package com.locator_app.locator.service.users;

import com.locator_app.locator.service.Api;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public interface UsersApi {

    @POST(Api.version + "/users/login")
    Observable<Response<LoginResponse>> login(@Body LoginRequest loginBodyRequest);

    @GET(Api.version + "/users/logout")
    Observable<Response<LogoutResponse>> logout();

    @GET(Api.version + "/users/protected")
    Observable<Response<Object>> requestProtected();

    @POST(Api.version + "/users/register")
    Observable<Response<RegistrationResponse>> register(@Body RegistrationRequest registrationBodyRequest);

}
