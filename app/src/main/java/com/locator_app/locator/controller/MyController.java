package com.locator_app.locator.controller;


import com.locator_app.locator.apiservice.my.BubbleScreenResponse;
import com.locator_app.locator.apiservice.my.MyApiService;

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
