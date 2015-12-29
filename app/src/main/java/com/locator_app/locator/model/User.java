package com.locator_app.locator.model;

public class User {

    public String mail = "";

    public String residence = "";

    public String name = "";

    public String _id = "";

    public boolean loggedIn = false;


    private static User _me;
    public static User me() {
        if (_me == null) {
            _me = new User();
        }
        return _me;
    }
}
