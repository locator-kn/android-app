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
import com.locator_app.locator.model.impressions.Impression;

import java.io.IOException;

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
                                    Toast.makeText(LocatorApplication.getAppContext(),
                                            "created image-impression", Toast.LENGTH_SHORT).show();
                                },
                                (err) -> {
                                    Toast.makeText(LocatorApplication.getAppContext(),
                                            "Deine Impression konnte leider nicht hochgeladen werden :-(",
                                            Toast.LENGTH_SHORT).show();
                                }
                        );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        finish();
    }
}
