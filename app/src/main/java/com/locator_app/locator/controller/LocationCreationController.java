package com.locator_app.locator.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import com.locator_app.locator.view.locationcreation.LocationSuggestions;

public class LocationCreationController {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Intent intent;

    static public void createLocation(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        activity.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    static public void onActivityResult(int requestCode, int resultCode, Intent data, Activity activity) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Intent intent = new Intent(activity, LocationSuggestions.class);

            intent.putExtra("picture", imageBitmap);
            activity.startActivity(intent);
        }
    }
}
