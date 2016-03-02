package com.locator_app.locator.service;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;

import com.google.android.gms.location.LocationServices;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CameraService {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 672;
    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 345;

    Activity activity;

    public CameraService(Activity activity) {
        this.activity = activity;
    }

    final List<Observer> uriSubscribers = new LinkedList<>();
    Observable<Uri> uriObservable = Observable.create(uriSubscribers::add);

    public Observable<Uri> takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);

                return uriObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }
        return goToCamera();
    }

    private Observable<Uri> goToCamera() {
        ContentValues values = new ContentValues();
        Uri imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

        return Observable.just(imageUri);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                notifyObserversWithUri();
                return;
            }
            notifyObserversWithError();
        }
    }

    private void notifyObserversWithUri() {
        synchronized (uriSubscribers) {
            for (Observer sub : uriSubscribers) {
                sub.onNext(goToCamera());
                sub.onCompleted();
            }
            uriSubscribers.clear();
        }
    }

    private void notifyObserversWithError() {
        synchronized (uriSubscribers) {
            for (Observer sub : uriSubscribers) {
                sub.onError(new Exception());
            }
            uriSubscribers.clear();
        }
    }
}
