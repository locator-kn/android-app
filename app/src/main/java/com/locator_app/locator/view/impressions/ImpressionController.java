package com.locator_app.locator.view.impressions;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.model.impressions.Impression;

public class ImpressionController extends Activity {

    public static final int IMAGE = 100;
    public static final int VIDEO = 200;
    private String locationId;

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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            finish();
            return;
        }
        if (requestCode == IMAGE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            LocationController.getInstance().createImageImpression(locationId, imageBitmap)
                    .subscribe(
                            (val) -> {
                                Toast.makeText(LocatorApplication.getAppContext(),
                                        "created image-impression", Toast.LENGTH_SHORT).show();
                            },
                            (err) -> {
                                Toast.makeText(LocatorApplication.getAppContext(),
                                        err.toString(), Toast.LENGTH_SHORT).show();
                            }
                    );
        }
        finish();
    }
}
