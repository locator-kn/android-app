package com.locator_app.locator.service;

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
    private Activity activity;
    private Uri imageUri;
    private CameraService cameraService;

    public LocationCreationController(Activity activity) {
        this.activity = activity;
        cameraService = new CameraService(activity);
    }

    public void startLocationCreation() {
        cameraService.takePhoto().subscribe((uri) -> imageUri = uri,
                                            (error) -> {});
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK &&
                requestCode == CameraService.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Intent intent = new Intent(activity, LocationSuggestions.class);

            intent.putExtra("imageUri", imageUri);
            activity.startActivity(intent);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        cameraService.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static final int LOCATION_CREATED = 500;

    public static final int LOCATION_CREATION_REQUEST = 12;
}
