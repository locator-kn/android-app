package com.locator_app.locator.controller;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.locator_app.locator.apiservice.locations.LocationsNearbyResponse;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.util.BitmapHelper;
import com.locator_app.locator.view.locationcreation.LocationSuggestions;

import java.io.IOException;

public class LocationCreationController {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Activity activity;
    private Uri imageUri;

    public LocationCreationController(Activity activity) {
        this.activity = activity;
    }

    public void createLocation() {
        ContentValues values = new ContentValues();
        imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK &&
                requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Intent intent = new Intent(activity, LocationSuggestions.class);

            intent.putExtra("imageUri", imageUri);
            activity.startActivity(intent);
        }
    }
}
