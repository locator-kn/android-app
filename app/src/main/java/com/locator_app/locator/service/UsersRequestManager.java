package com.locator_app.locator.service;

import com.locator_app.locator.model.User;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class UsersRequestManager {

    UsersApiService service = new UsersApiService();
    LoginRequest loginRequest;

    public Observable<LoginResponse> register(RegistrationRequest request) {
        return service.register(request)
                .doOnNext(registrationResponse -> loginRequest = LoginRequest.fromRegistrationRequest(request))
                .flatMap(this::makeLoginRequest);
    }

    private Observable<LoginResponse> makeLoginRequest(RegistrationResponse response) {
        return login(loginRequest);
    }

    public Observable<LoginResponse> login(LoginRequest request) {
        return service.login(request)
                .doOnNext(this::handleLogin);
    }

    private void handleLogin(LoginResponse loginResponse) {
        User.me()._id = loginResponse._id;
        User.me().name = loginResponse.name;
        User.me().mail = loginResponse.mail;
        User.me().residence = loginResponse.residence;
        User.me().loggedIn = true;
    }

    public Observable<LogoutResponse> logout() {
        return service.logout()
                .doOnNext(this::handleLogout);
    }

    private void handleLogout(LogoutResponse logoutResponse) {
        User.me()._id = "";
        User.me().name = "";
        User.me().mail = "";
        User.me().residence = "";
        User.me().loggedIn = false;
    }
}
