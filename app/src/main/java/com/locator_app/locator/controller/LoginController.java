package com.locator_app.locator.controller;

import com.locator_app.locator.model.Login;


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