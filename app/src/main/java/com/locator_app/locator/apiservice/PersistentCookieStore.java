package com.locator_app.locator.apiservice;

import android.content.SharedPreferences;

import com.locator_app.locator.LocatorApplication;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class PersistentCookieStore implements CookieStore {

    List<HttpCookie> cookies = new LinkedList<>();

    public void add(URI uri, HttpCookie cookie) {
        if (cookie.getName().equals("locator_session")) {
            cookies.clear();
            cookies.add(cookie);
            SharedPreferences preferences = LocatorApplication.getSharedPreferences();
            preferences.edit().putString("locator_session", cookie.getValue()).apply();
        }
    }

    public PersistentCookieStore() {
        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        String sessionKey = preferences.getString("locator_session", "");
        if (!sessionKey.isEmpty()) {
            cookies.add(new HttpCookie("locator_session", sessionKey));
        }
    }

    public List<HttpCookie> get(URI uri) {
        if (uri.toString().startsWith(Api.serverUrl)) {
            return cookies;
        }
        return new LinkedList<>();
    }

    public boolean removeAll() {
        cookies.clear();
        return true;
    }

    public List<HttpCookie> getCookies() {
        return cookies;
    }

    public List<URI> getURIs() {
        List<URI> uris = new LinkedList<>();
        if (!cookies.isEmpty()) {
            try {
                uris.add(new URI(Api.serverUrl));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return uris;
    }

    public boolean remove(URI uri, HttpCookie cookie) {
        return uri.toString().startsWith(Api.serverUrl) && cookies.remove(cookie);
    }
}
