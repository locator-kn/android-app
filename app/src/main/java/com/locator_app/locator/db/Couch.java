package com.locator_app.locator.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Revision;
import com.couchbase.lite.SavedRevision;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.android.AndroidContext;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.model.User;
import com.locator_app.locator.util.BitmapHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    }

    private boolean isLocalImage(String imageUri) {
        return imageUri.startsWith("drawable://");
    }

    public void deleteAllImages() {
        Document doc = database.getDocument("images");
        if (doc != null) {
            SavedRevision savedRevision = doc.getCurrentRevision();
            if (savedRevision != null) {
                UnsavedRevision unsavedRevision = savedRevision.createRevision();
                for (String attachment: savedRevision.getAttachmentNames()) {
                    unsavedRevision.removeAttachment(attachment);
                    Log.d("Couch", "removed attachment " + attachment);
                }
            }
        }
    }

    public void storeImage(String imageUri, Bitmap image) {
        if (isLocalImage(imageUri))
            return;
        Document task = database.getDocument("images");
        UnsavedRevision newRev = task.getCurrentRevision().createRevision();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        newRev.setAttachment(imageUri, "image/jpeg", in);
        try {
            newRev.save();
            Log.d("Couch", "stored image to database: " + imageUri);
        } catch (CouchbaseLiteException e) {
            Log.d("Couch", "could not store image: " + imageUri);
        }
    }

    public Bitmap image(String imageUri) {
        if (isLocalImage(imageUri))
            return null;
        Document task = database.getDocument("images");
        Revision rev = task.getCurrentRevision();
        Attachment att = rev.getAttachment(imageUri);
        if (att == null) {
            Log.d("Couch", "found no attachment for image " + imageUri);
            return null;
        }
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(att.getContent());
            Log.d("Couch", "loaded image from couch " + imageUri);
            return bitmap;
        } catch (CouchbaseLiteException e) {
            Log.d("Couch", "could not decode input stream from Attachment.getContent()");
        }
        return null;
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

    public void switchToDefaultDatabase() {
        switchToDatabase("default");
    }

    public void switchToDatabase(String databaseName) {
        try {
            databaseName = databaseName.replace("@", "_at_");
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
