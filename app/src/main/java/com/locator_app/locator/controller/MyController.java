package com.locator_app.locator.controller;


import android.util.Log;

import com.locator_app.locator.apiservice.my.BubbleScreenResponse;
import com.locator_app.locator.apiservice.my.MyApiService;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyController {

    MyApiService myService = null;

    public Observable<BubbleScreenResponse> getBubbleScreen() {
        return myService.bubbleScreen()
                .doOnError(this::onBubbleScreenError)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void onBubbleScreenError(Throwable throwable) {
        Log.d("MyController", throwable.getMessage());
    }

    public Observable<Object> changePassword(String oldPassword, String newPassword) {
        return myService.changePassword(oldPassword, newPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
