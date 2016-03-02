package com.locator_app.locator.controller;


import com.locator_app.locator.apiservice.users.LoginRequest;
import com.locator_app.locator.apiservice.users.LogoutResponse;
import com.locator_app.locator.apiservice.users.RegistrationRequest;
import com.locator_app.locator.apiservice.users.UsersApiService;
import com.locator_app.locator.model.User;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserController {

    private User me;
    private UsersApiService userService;

    public boolean loggedIn() {
        return me != null;
    }

    public User me() {
        assert me != null;
        return me;
    }

    public Observable<User> register(RegistrationRequest registrationRequest) {
        return userService.register(registrationRequest)
                .doOnNext(this::handleLogin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<User> login(LoginRequest loginRequest) {
        return userService.login(loginRequest)
                .doOnNext(this::handleLogin)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<User> checkProtected() {
        return userService.checkProtected()
                .doOnNext(this::handleLogin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void handleLogin(User user) {
        me = user;
    }

    public Observable<LogoutResponse> logout() {
        return userService.logout()
                .doOnNext(this::handleLogout)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void handleLogout(LogoutResponse logoutResponse) {
        me = null;
    }

    public Observable<User> followUser(String userId) {
        return userService.followUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<User> getUser(String userId) {
        return userService.getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<User> getFollowers(String userId) {
        return userService.getFollowers(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static UserController instance;
    public static UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }

    private UserController() {
        userService = new UsersApiService();
    }
}
