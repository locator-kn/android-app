package com.locator_app.locator.controller;


import android.content.SharedPreferences;
import android.util.Log;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.db.Couch;
import com.locator_app.locator.model.User;
import com.locator_app.locator.apiservice.users.LoginRequest;
import com.locator_app.locator.apiservice.users.LoginResponse;
import com.locator_app.locator.apiservice.users.LogoutResponse;
import com.locator_app.locator.apiservice.users.RegistrationRequest;
import com.locator_app.locator.apiservice.users.UsersApiService;

import rx.Observable;

public class UserController {

    private final String lastLoggedInUserEmailKey = "lastLoggedInUser";
    private UsersApiService userService;
    private User me;
    private boolean loggedIn = false;
    public User me() {
        return this.me;
    }

    public Observable<LoginResponse> register(RegistrationRequest registrationRequest) {
        return userService.register(registrationRequest)
                .doOnNext(this::handleLogin);
    }

    public Observable<LoginResponse> login(LoginRequest loginRequest) {
        return userService.login(loginRequest)
                .doOnNext(this::handleLogin);
    }

    public Observable<LoginResponse> logInLastLoggedInUser() {
        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        String lastLoggedInUserEmail = preferences.getString(lastLoggedInUserEmailKey, "");
        if (lastLoggedInUserEmail.isEmpty()) {
            return Observable.error(new Exception("no user was logged in"));
        } else {
            Couch.get().switchToDatabase(lastLoggedInUserEmail);
            Couch.get().restore(me);
            return userService.checkProtected()
                    .doOnNext(this::handleLogin);
        }
    }

    public boolean loggedIn() {
        return loggedIn;
    }

    private void handleLogin(LoginResponse loginResponse) {
        Log.d("UserController", "successfully logged in: " + loginResponse.mail);
        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        preferences.edit().putString(lastLoggedInUserEmailKey, loginResponse.mail).apply();
        me._id = loginResponse._id;
        me.name = loginResponse.name;
        me.mail = loginResponse.mail;
        me.residence = loginResponse.residence;
        loggedIn = true;
        Couch.get().switchToDatabase(me.mail);
        Couch.get().save(me);
    }

    public Observable<LogoutResponse> logout() {
        return userService.logout()
                .doOnNext(this::handleLogout);
    }

    private void handleLogout(LogoutResponse logoutResponse) {
        loggedIn = false;
        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        preferences.edit().remove(lastLoggedInUserEmailKey).apply();
        me._id = "";
        me.name = "";
        me.mail = "";
        me.residence = "";
        Couch.get().switchToDefaultDatabase();
    }


    public Observable<User> getUser(String userId) {
        return userService.getUser(userId);
    }

    public Observable<User> getFollowers(String userId) {
        return userService.getFollowers(userId);
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
