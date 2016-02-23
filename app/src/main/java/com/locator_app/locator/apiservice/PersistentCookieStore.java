package com.locator_app.locator.apiservice;

import android.content.SharedPreferences;

import com.locator_app.locator.LocatorApplication;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PersistentCookieStore implements CookieStore {

    public static final String LOCATOR_USER_COOKIE = "locator_session";
    public static final String LOCATOR_DEVICE_COOKIE = "locator";

    final List<HttpCookie> cookies = new LinkedList<>();
    final List<String> supportedCookieNames = Arrays.asList(LOCATOR_USER_COOKIE,
                                                            LOCATOR_DEVICE_COOKIE);

    public void add(URI uri, HttpCookie cookie) {
        final String cookieName = cookie.getName();
        if (supportedCookieNames.contains(cookieName)) {
            removeCookieByName(cookieName);
            addCookie(cookie);
            storeCookie(cookie);
        }
    }

    private void removeCookieByName(String cookieName) {
        for (HttpCookie cookie: cookies) {
            if (cookie.getName().equals(cookieName)) {
                cookies.remove(cookie);
                break;
            }
        }
    }

    private void addCookie(HttpCookie cookie) {
        if (!cookie.getValue().isEmpty()) {
            cookies.add(cookie);
        }
    }

    private void storeCookie(HttpCookie cookie) {
        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        preferences.edit().putString(cookie.getName(), cookie.getValue()).apply();
    }

    public PersistentCookieStore() {
        SharedPreferences preferences = LocatorApplication.getSharedPreferences();
        for (String cookieName: supportedCookieNames) {
            String cookieValue = preferences.getString(cookieName, "");
            addCookie(new HttpCookie(cookieName, cookieValue));
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
