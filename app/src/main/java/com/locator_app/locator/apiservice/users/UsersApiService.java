package com.locator_app.locator.apiservice.users;


import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.model.User;

import java.net.UnknownHostException;
import java.util.List;

import retrofit.HttpException;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class UsersApiService {

    public interface UsersApi {

        @POST(Api.version + "/users/login")
        Observable<Response<LoginResponse>> login(@Body LoginRequest loginBodyRequest);

        @GET(Api.version + "/users/logout")
        Observable<Response<LogoutResponse>> logout();

        @GET(Api.version + "/users/protected")
        Observable<Response<LoginResponse>> requestProtected();

        @POST(Api.version + "/users/register")
        Observable<Response<RegistrationResponse>> register(@Body RegistrationRequest registrationBodyRequest);

        @GET(Api.version + "/users/{userId}")
        Observable<Response<User>> getUser(@Path("userId") String userId);

        @GET(Api.version + "/users/{userId}/follower")
        Observable<Response<List<User>>> getFollowers(@Path("userId") String userId);
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

    public Observable<User> getUser(String userId) {
        return service.getUser(userId)
                .flatMap(this::parseUserResponse);
    }

    private Observable<User> parseUserResponse(Response<User> userResponse) {
        if (userResponse.isSuccess()) {
            return Observable.just(userResponse.body());
        }
        return Observable.error(new Exception("http-error: " + Integer.toString(userResponse.code())));
    }

    public Observable<User> getFollowers(String userId) {
        return service.getFollowers(userId)
                .flatMap(this::parseFollowersResponse);
    }

    private Observable<User> parseFollowersResponse(Response<List<User>> response) {
        if (response.isSuccess()) {
            return Observable.from(response.body());
        }
        return Observable.error(new Exception("http-error: " + Integer.toString(response.code())));
    }

    public Observable<LoginResponse> checkProtected() {
        return service.requestProtected()
                .flatMap(this::parseProtectedResponse);
    }

    private Observable<LoginResponse> parseProtectedResponse(Response<LoginResponse> response) {
        if (response.isSuccess()) {
            return Observable.just(response.body());
        }
        if (response.code() == 401) {
            return Observable.error(new Exception("you are not authorized"));
        }
        return Observable.error(new Exception("http-code: " + Integer.toString(response.code())));
    }
}
