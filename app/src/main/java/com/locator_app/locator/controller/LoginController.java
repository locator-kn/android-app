package com.locator_app.locator.controller;

import com.locator_app.locator.model.Login;
import com.locator_app.locator.service.LoginService;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.rest.RestService;


@EBean(scope = EBean.Scope.Singleton)
public class LoginController {
    @RestService
    LoginService loginService;
    private Login login;

    /*private static LoginController instance;

    public LoginController() {
        login = new Login();
    }

    public static LoginController getInstance() {
        if (instance == null) {
            instance = new LoginController();
        }

        return instance;
    }*/

    public void login() {
        loginService.login();
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