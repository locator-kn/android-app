package com.locator_app.locator.view.locationcreation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.service.CameraService;
import com.locator_app.locator.view.locationcreation.LocationSuggestions;

public class LocationCreationController {
    private Activity activity;
    private Uri imageUri;
    private CameraService cameraService;

    public LocationCreationController(Activity activity) {
        this.activity = activity;
        cameraService = new CameraService(activity);
    }

    public void startLocationCreation() {
        if (UserController.getInstance().loggedIn()) {
            cameraService.takePhoto().subscribe((uri) -> imageUri = uri,
                    (error) -> {});
        } else {
            Toast.makeText(activity, "Whooop whooop! Erst anmelden!", Toast.LENGTH_SHORT).show();
        }
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
