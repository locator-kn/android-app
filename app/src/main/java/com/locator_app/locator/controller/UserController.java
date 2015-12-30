package com.locator_app.locator.controller;


import com.locator_app.locator.db.Couch;
import com.locator_app.locator.model.User;
import com.locator_app.locator.service.LoginRequest;
import com.locator_app.locator.service.LoginResponse;
import com.locator_app.locator.service.LogoutResponse;
import com.locator_app.locator.service.RegistrationRequest;
import com.locator_app.locator.service.RegistrationResponse;
import com.locator_app.locator.service.UsersApiService;

import rx.Observable;

public class UserController {

    private UsersApiService userService;
    private User me;

    public User me() {
        return this.me;
    }

    public Observable<LoginResponse> register(RegistrationRequest registrationRequest) {
        return userService.register(registrationRequest)
                .doOnError(this::handleRegistrationError)
                .doOnNext(this::handleRegistration)
                .map(registrationResponse -> LoginRequest.fromRegistrationRequest(registrationRequest))
                .flatMap(loginRequest -> login(loginRequest));
    }

    private void handleRegistrationError(Throwable throwable) {

    }

    private void handleRegistration(RegistrationResponse registrationResponse) {

    }

    public Observable<LoginResponse> login(LoginRequest loginRequest) {
        return userService.login(loginRequest)
                .doOnError(this::handleLoginError)
                .doOnNext(this::handleLogin);
    }

    private void handleLogin(LoginResponse loginResponse) {
        me._id = loginResponse._id;
        me.name = loginResponse.name;
        me.mail = loginResponse.mail;
        me.residence = loginResponse.residence;
        me.loggedIn = true;
        Couch.get().onLogin(me);
    }

    private void handleLoginError(Throwable throwable) {

    }

    public Observable<LogoutResponse> logout() {
        return userService.logout()
                .doOnError(this::handleLogoutError)
                .doOnNext(this::handleLogout);
    }

    private void handleLogout(LogoutResponse logoutResponse) {
        me._id = "";
        me.name = "";
        me.mail = "";
        me.residence = "";
        me.loggedIn = false;
        Couch.get().onLogout();
    }

    private void handleLogoutError(Throwable throwable) {

    }

    private static UserController instance;
    public static UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }
    private UserController() {
        me = new User();
        userService = new UsersApiService();
    }
}
