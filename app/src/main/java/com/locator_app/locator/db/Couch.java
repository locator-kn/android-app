package com.locator_app.locator.db;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class Couch {

    private Manager manager;
    private Database database;

    public void save(User user) {
        Document doc = database.getDocument("user");
        Map<String, Object> properties = new HashMap<>();
        if (doc.getProperties() != null) {
            properties.putAll(doc.getProperties());
        }
        properties.put("id", user._id);
        properties.put("name", user.name);
        properties.put("mail", user.mail);
        properties.put("residence", user.residence);
        properties.put("loggedIn", true);
        try {
            doc.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void restore(User user) {
        Document doc = database.getDocument("user");
        Map<String, Object> properties = new HashMap<>();
        if (doc.getProperties() != null) {
            properties.putAll(doc.getProperties());
        }
        user.name = get(properties, "name", "");
        user.mail = get(properties, "mail", "");
        user.residence = get(properties, "residence", "");
        user._id = get(properties, "id", "");
        user.loggedIn = get(properties, "loggedIn", false);
    }

    public void onLogin(User user) {
        String dbName = user.mail.replace("@", "");
        switchToDatabase(dbName);
        save(user);
    }

    public void onAppStart(User user) {
        for (String db: manager.getAllDatabaseNames()) {
            if (isUserLoggedIn(db)) {
                restore(user);
                break;
            }
        }
    }

    private boolean isUserLoggedIn(String db) {
        User user = new User();
        switchToDatabase(db);
        restore(user);
        return user.loggedIn;
    }

    public void onLogout() {
        Document doc = database.getDocument("user");
        Map<String, Object> properties = new HashMap<>();
        if (doc.getProperties() != null) {
            properties.putAll(doc.getProperties());
        }
        properties.put("loggedIn", false);
        try {
            doc.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        switchToDefaultDatabase();
    }

    private <T> T get(Map<String, Object> map, String key, T defaultValue) {
        if (!map.containsKey(key))
            return defaultValue;
        Object obj = map.get(key);
        if (obj == null)
            return defaultValue;
        if (obj.getClass() != defaultValue.getClass())
            return defaultValue;
        return (T) obj;
    }

    private void switchToDefaultDatabase() {
        switchToDatabase("default");
    }

    private void switchToDatabase(String databaseName) {
        try {
            if (database == null || !database.getName().equals(databaseName)) {
                database = manager.getDatabase(databaseName);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllDatabases() {
        for (String db: manager.getAllDatabaseNames()) {
            switchToDatabase(db);
            try {
                database.delete();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }

    private static Couch instance;
    public static Couch get() {
        if (instance == null) {
            instance = new Couch();
        }
        return instance;
    }

    private Couch() {
        try {
            manager = new Manager(new AndroidContext(LocatorApplication.getAppContext()),
                    Manager.DEFAULT_OPTIONS);
            switchToDefaultDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
