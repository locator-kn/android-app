package com.locator_app.locator.apiservice.users;


import android.graphics.Bitmap;

import com.locator_app.locator.apiservice.Api;
import com.locator_app.locator.apiservice.ServiceFactory;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;
import com.locator_app.locator.model.User;
import com.locator_app.locator.util.BitmapHelper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.List;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import rx.Observable;

public class UsersApiService {

    public interface UsersApi {

        @POST(Api.version + "/users/login")
        Observable<Response<User>> login(@Body LoginRequest loginBodyRequest);

        @POST(Api.version + "/users/facebooklogin")
        Observable<Response<User>> facebooklogin(@Body FacebookLoginRequest facebookLoginRequest);

        @GET(Api.version + "/users/logout")
        Observable<Response<LogoutResponse>> logout();

        @GET(Api.version + "/users/protected")
        Observable<Response<User>> requestProtected();

        @POST(Api.version + "/users/register")
        Observable<Response<User>> register(@Body RegistrationRequest registrationBodyRequest);

        @Multipart
        @POST(Api.version + "/users/register/image")
        Observable<Response<Object>> setProfilePicture(@Part("file\"; filename=image.jpg") RequestBody file);

        @Headers("Cache-Control: max-age=10")
        @GET(Api.version + "/users/{userId}?count=locations,followers")
        Observable<Response<User>> getUser(@Path("userId") String userId);

        @GET(Api.version + "/users/{userId}/follower")
        Observable<Response<List<User>>> getFollowers(@Path("userId") String userId);

        @POST(Api.version + "/users/{userId}/follow")
        Observable<Response<User>> followUser(@Path("userId") String userId);
    }

    private UsersApi service = ServiceFactory.createService(UsersApi.class);

    public Observable<User> login(LoginRequest loginRequest) {
        return GenericErrorHandler.wrapSingle(service.login(loginRequest));
    }

    public Observable<User> facebooklogin(String token) {
        return GenericErrorHandler.wrapSingle(service.facebooklogin(new FacebookLoginRequest(token)));
    }

    public Observable<LogoutResponse> logout() {
        return GenericErrorHandler.wrapSingle(service.logout());
    }

    public Observable<User> register(RegistrationRequest request) {
        return GenericErrorHandler.wrapSingle(service.register(request));
    }

    public Observable<Object> setProfilePicture(Bitmap image) {
        File jpgFile = BitmapHelper.toJpgFile(image);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), jpgFile);
        return GenericErrorHandler.wrapSingle(service.setProfilePicture(requestBody));

    }

    public Observable<User> getUser(String userId) {
        return GenericErrorHandler.wrapSingle(service.getUser(userId));
    }

    public Observable<User> getFollowers(String userId) {
        return GenericErrorHandler.wrapList(service.getFollowers(userId));
    }

    public Observable<User> checkProtected() {
        return GenericErrorHandler.wrapSingle(service.requestProtected());
    }

    public Observable<User> followUser(String userId) {
        return GenericErrorHandler.wrapSingle(service.followUser(userId));
    }
}
