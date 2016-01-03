package com.locator_app.locator.controller;


import com.locator_app.locator.model.User;
import com.locator_app.locator.service.my.BubbleScreenResponse;
import com.locator_app.locator.service.my.MyApiService;
import com.locator_app.locator.service.users.UsersApiService;

import rx.Observable;

public class MyController {

    MyApiService myService = null;

    public Observable<BubbleScreenResponse> getBubbleScreen() {
        return myService.bubbleScreen();
    }

    private static MyController instance;
    public static MyController getInstance() {
        if (instance == null) {
            instance = new MyController();
        }
        return instance;
    }
    private MyController() {
        myService = new MyApiService();
    }
}
