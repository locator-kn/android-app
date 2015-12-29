package com.locator_app.locator.controller;

import android.widget.Toast;

import com.locator_app.locator.model.Login;
import com.locator_app.locator.service.LoginRequest;
import com.locator_app.locator.service.LoginResponse;
import com.locator_app.locator.service.LoginService;
import com.locator_app.locator.service.UsersRequestManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.rest.RestService;

import rx.Observer;


public class LoginController {

    private Login login;

    private static LoginController instance;

    private LoginController() {
        login = new Login();
    }

    public static LoginController getInstance() {
        if (instance == null) {
            instance = new LoginController();
        }

        return instance;
    }

    public void setMail(String mail) {
        this.login.setMail(mail);
    }

    public String getMail() {
        return this.login.getMail();
    }

    public void setPassword(String password) {
        this.login.setPassword(password);
    }

    public String getPassword() {
        return this.login.getPassword();
    }
}