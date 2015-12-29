package com.locator_app.locator.service;


import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.net.UnknownHostException;

import retrofit.HttpException;
import retrofit.Response;
import rx.Observable;

public class UsersApiService {

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
        LogoutResponse parsedResponse = (LogoutResponse)
                APIUtils.parseResponse(response, LogoutResponse.class);
        if (parsedResponse != null) {
            return Observable.just(parsedResponse);
        } else {
            return Observable.error(new Exception());
        }
    }

    public Observable<RegistrationResponse> register(RegistrationRequest request) {
        return service.register(request)
                .doOnError(this::handleError)
                .flatMap(this::parseRegisterResponse);
    }

    private Observable<RegistrationResponse> parseRegisterResponse(Response response) {
        RegistrationResponse parsedResponse = (RegistrationResponse)
                APIUtils.parseResponse(response, RegistrationResponse.class);
        if (parsedResponse != null) {
            return Observable.just(parsedResponse);
        } else {
            return Observable.error(new Exception());
        }
    }

    private void handleError(Throwable throwable) {
        Log.d(UsersApiService.class.getName(), "handleError", throwable);
        if (throwable instanceof HttpException) {
            HttpException ex = (HttpException) throwable;
        } else if (throwable instanceof UnknownHostException) {
            UnknownHostException ex = (UnknownHostException) throwable;
        } else if (throwable instanceof JsonSyntaxException) {
            JsonSyntaxException ex = (JsonSyntaxException) throwable;
        }
    }
}
