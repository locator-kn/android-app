package com.locator_app.locator.service.users;


import com.google.gson.JsonSyntaxException;
import com.locator_app.locator.service.Api;
import com.locator_app.locator.service.ServiceFactory;

import java.net.UnknownHostException;

import retrofit.HttpException;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public class UsersApiService {

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

    private UsersApi service = ServiceFactory.createService(UsersApi.class);

    public Observable<LoginResponse> login(LoginRequest loginRequest) {
        return service.login(loginRequest)
                .doOnError(this::handleError)
                .flatMap(this::parseLoginResponse);
    }

    private Observable<LoginResponse> parseLoginResponse(Response response) {
        if (response.isSuccess()) {
            return Observable.just((LoginResponse)response.body());
        }
        if (response.code() == 401) {
            return Observable.error(new Exception("wrong username or password"));
        }
        return Observable.error(new Exception("http-error: " + Integer.toString(response.code())));
    }

    public Observable<LogoutResponse> logout() {
        return service.logout()
                .doOnError(this::handleError)
                .flatMap(this::parseLogoutResponse);
    }

    private Observable<LogoutResponse> parseLogoutResponse(Response response) {
        if (response.isSuccess()) {
            return Observable.just((LogoutResponse)response.body());
        }
        return Observable.error(new Exception("http-error: " + Integer.toString(response.code())));
    }

    public Observable<RegistrationResponse> register(RegistrationRequest request) {
        return service.register(request)
                // todo: error handling
                //.doOnError(this::handleRegistrationError)
                // here we mask the error (i.e. a cheesy response body (string "user created"))
                .onErrorReturn(error -> {
                    RegistrationResponse registrationResponse = new RegistrationResponse();
                    return Response.success(registrationResponse);
                })
                .flatMap(this::parseRegistrationResponse);
    }

    private Observable<RegistrationResponse> parseRegistrationResponse(Response response) {
        if (response.isSuccess()) {
            return Observable.just((RegistrationResponse) response.body());
        }
        return Observable.error(new Exception("http-error: " + Integer.toString(response.code())));
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException ex = (HttpException) throwable;
        } else if (throwable instanceof UnknownHostException) {
            UnknownHostException ex = (UnknownHostException) throwable;
        } else if (throwable instanceof JsonSyntaxException) {
            JsonSyntaxException ex = (JsonSyntaxException) throwable;
        }
    }
}
