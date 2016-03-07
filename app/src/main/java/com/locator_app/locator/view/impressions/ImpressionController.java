package com.locator_app.locator.view.impressions;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.model.impressions.AbstractImpression;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;

public class ImpressionController extends Activity {

    public static final int IMAGE = 100;
    public static final int VIDEO = 200;
    public static final int TEXT = 300;
    private String locationId;
    private Uri imageUri;
    File videoFile;
    MediaRecorder mediaRecorder;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impression_controller);
        imageView = (ImageView)findViewById(R.id.imageView);

        locationId = getIntent().getStringExtra("locationId");
        String type = getIntent().getStringExtra("type");
        if (type.equals("image")) {
            createImageImpression();
        } else if (type.equals("video")) {
            createVideoImpression();
        } else if (type.equals("text")) {
            createTextImpression();
        } else {
            finish();
        }
    }

    private void createTextImpression() {
        Intent intent = new Intent(this, TextImpressionActivity.class);
        intent.putExtra("locationId", locationId);
        startActivityForResult(intent, TEXT);
    }

    private void createImageImpression() {
        ContentValues values = new ContentValues();
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE);
    }

    private void createVideoImpression() {
        videoFile = new File(getExternalCacheDir(), "videoimpression.3gp");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 6291456L);
        startActivityForResult(intent, VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            finish();
            return;
        }
        if (requestCode == IMAGE) {
            doUploadImage();
        } else if (requestCode == VIDEO) {
            doUploadVideo();
        } else if (requestCode == TEXT) {
            checkTextImpressionActivityResult(data);
        }
    }

    private void checkTextImpressionActivityResult(Intent data) {
        if (data.getBooleanExtra("success", false)) {
            notify(AbstractImpression.ImpressionType.TEXT);
        } else {
            notifyError(AbstractImpression.ImpressionType.IMAGE,
                    new Throwable("Dein Kommentar konnte leider nicht hochgeladen werden"));
        }
        finish();
    }

    private void doUploadImage() {
        spin();

        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            LocationController.getInstance().createImageImpression(locationId, imageBitmap)
                    .subscribe(
                            (val) -> {
                                notify(AbstractImpression.ImpressionType.IMAGE);
                                finish();
                            },
                            (err) -> {
                                notifyError(AbstractImpression.ImpressionType.IMAGE,
                                        new Throwable("Dein Bild konnte leider nicht hochgeladen werden"));
                                finish();
                            }
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doUploadVideo() {
        spin();

        try {
            byte[] data = FileUtils.readFileToByteArray(videoFile);
            LocationController.getInstance().createVideoImpression(locationId, data)
                    .subscribe(
                            (val) -> {
                                notify(AbstractImpression.ImpressionType.VIDEO);
                                finish();
                            },
                            (err) -> {
                                notifyError(AbstractImpression.ImpressionType.VIDEO,
                                        new Throwable("Dein Video konnte leider nicht hochgeladen werden"));
                                finish();
                            }
                    );
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    private void spin() {
        Glide.with(this).load(R.drawable.preloader)
                .asGif()
                .into(imageView);
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
