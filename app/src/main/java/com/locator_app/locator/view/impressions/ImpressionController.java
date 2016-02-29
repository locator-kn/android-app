package com.locator_app.locator.view.impressions;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.model.impressions.Impression;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImpressionController extends Activity {

    public static final int IMAGE = 100;
    public static final int VIDEO = 200;
    private String locationId;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationId = getIntent().getStringExtra("locationId");
        String type = getIntent().getStringExtra("type");
        if (type.equals("image")) {
            createImageImpression();
        } else {
            finish();
        }
    }

    public void createImageImpression() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "impression");
        values.put(MediaStore.Images.Media.DESCRIPTION, "new image impression");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            finish();
            return;
        }
        if (requestCode == IMAGE) {
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                LocationController.getInstance().createImageImpression(locationId, imageBitmap)
                        .subscribe(
                                (val) -> {
                                    notify(AbstractImpression.ImpressionType.IMAGE);
                                },
                                (err) -> {
                                    notifyError(AbstractImpression.ImpressionType.IMAGE,
                                            new Throwable("Dein Bild konnte leider nicht hochgeladen werden"));
                                }
                        );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        finish();
    }


    private static Set<ImpressionObserver> observers = new HashSet<>();
    public static void addImpressionObserver(ImpressionObserver obs) {
        observers.add(obs);
    }

    public static void removeImpressionObserver(ImpressionObserver obs) {
        observers.remove(obs);
    }

    private static void notify(AbstractImpression.ImpressionType type) {
        for (ImpressionObserver obs: observers) {
            obs.onImpressionCreated(type);
        }
    }

    private static void notifyError(AbstractImpression.ImpressionType type, Throwable error) {
        for (ImpressionObserver obs: observers) {
            obs.onImpressionCreationFailed(type, error);
        }
    }
}
