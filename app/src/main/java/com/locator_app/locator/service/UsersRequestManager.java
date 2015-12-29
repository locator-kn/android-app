package com.locator_app.locator.service;

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
        return service.login(request);
    }

    public Observable<LogoutResponse> logout() {
        return service.logout();
    }
}
