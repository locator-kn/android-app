package com.locator_app.locator.apiservice.users;


import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;
import com.locator_app.locator.model.User;

import java.util.List;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public class UsersApiService {

    public interface UsersApi {

        @POST(Api.version + "/users/login")
        Observable<Response<LoginResponse>> login(@Body LoginRequest loginBodyRequest);

        @GET(Api.version + "/users/logout")
        Observable<Response<LogoutResponse>> logout();

        @GET(Api.version + "/users/protected")
        Observable<Response<LoginResponse>> requestProtected();

        @POST(Api.version + "/users/register")
        Observable<Response<LoginResponse>> register(@Body RegistrationRequest registrationBodyRequest);

        @GET(Api.version + "/users/{userId}")
        Observable<Response<User>> getUser(@Path("userId") String userId);

        @GET(Api.version + "/users/{userId}/follower")
        Observable<Response<List<User>>> getFollowers(@Path("userId") String userId);
    }

    private UsersApi service = ServiceFactory.createService(UsersApi.class);

    public Observable<LoginResponse> login(LoginRequest loginRequest) {
        return GenericErrorHandler.wrapSingle(service.login(loginRequest));
    }

    public Observable<LogoutResponse> logout() {
        return GenericErrorHandler.wrapSingle(service.logout());
    }

    public Observable<LoginResponse> register(RegistrationRequest request) {
        return GenericErrorHandler.wrapSingle(service.register(request));
    }

    public Observable<User> getUser(String userId) {
        return GenericErrorHandler.wrapSingle(service.getUser(userId));
    }

    public Observable<User> getFollowers(String userId) {
        return GenericErrorHandler.wrapList(service.getFollowers(userId));
    }

    public Observable<LoginResponse> checkProtected() {
        return GenericErrorHandler.wrapSingle(service.requestProtected());
    }
}
