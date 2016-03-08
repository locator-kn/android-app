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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.service.CameraService;
import com.locator_app.locator.util.BitmapHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;

public class ImpressionController extends Activity {

    public static final int VIDEO = 100;
    public static final int TEXT = 200;
    private String locationId;
    private Uri imageUri;
    File videoFile;

    ImageView imageView;
    CameraService cameraService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impression_controller);
        if (!UserController.getInstance().loggedIn()) {
            Toast.makeText(getApplicationContext(), "Log dich erst ein", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cameraService = new CameraService(this);
        imageView = (ImageView)findViewById(R.id.imageView);

        locationId = getIntent().getStringExtra("locationId");
        String type = getIntent().getStringExtra("type");
        switch (type) {
            case "image":
                createImageImpression();
                break;
            case "video":
                createVideoImpression();
                break;
            case "text":
                createTextImpression();
                break;
            default:
                finish();
                break;
        }
    }

    private void createTextImpression() {
        Intent intent = new Intent(this, TextImpressionActivity.class);
        intent.putExtra("locationId", locationId);
        startActivityForResult(intent, TEXT);
    }

    private void createImageImpression() {
        cameraService.takePhoto()
                .subscribe(
                        (uri) -> {
                            this.imageUri = uri;
                        },
                        (err) -> {
                            finish();
                        }
                );
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
        if (requestCode == CameraService.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
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

        Bitmap impression = BitmapHelper.get(imageUri, 1200, 1200);
        LocationController.getInstance().createImageImpression(locationId, impression)
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
