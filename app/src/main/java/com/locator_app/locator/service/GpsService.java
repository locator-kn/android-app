package com.locator_app.locator.service;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;

import java.net.ConnectException;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GpsService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private Activity activity;

    LocationSettingsRequest settingsRequest;

    private GoogleApiClient googleApiClient;

    public GpsService(Activity activity) {
        googleApiClient = new GoogleApiClient.Builder(LocatorApplication.getAppContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.activity = activity;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);
        settingsRequest = builder.build();
    }

    final List<Observer> currentLocationSubscribers = new LinkedList<>();
    Observable<Location> currentLocationObservable = Observable.create(currentLocationSubscribers::add);

    public Observable<android.location.Location> getCurLocation() {
        googleApiClient.connect();
        return currentLocationObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static final int PERMISSION_REQUEST_CODE = 69; // :D

    private void notifyObserversWithLocation() {
        synchronized (currentLocationSubscribers) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location == null) {
                if (gpsShouldBeOn) {
                    new Handler().postDelayed(() -> {
                        onConnected(null);
                    }, 2000);
                }
                return;
            }
            for (Observer sub : currentLocationSubscribers) {
                sub.onNext(location);
                sub.onCompleted();
            }
            googleApiClient.disconnect();
            currentLocationSubscribers.clear();
        }
    }

    private void notifyObserversWithError() {
        synchronized (currentLocationSubscribers) {
            for (Observer sub : currentLocationSubscribers) {
                sub.onError(new Exception());
            }
            googleApiClient.disconnect();
            currentLocationSubscribers.clear();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onConnected(null);
                return;
            }
            notifyObserversWithError();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
                return;
            }
        }

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, settingsRequest);
        result.setResultCallback(settingsResult -> {
            final Status status = settingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    gpsShouldBeOn = true;
                    notifyObserversWithLocation();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    gpsShouldBeOn = false;
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                                activity, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    gpsShouldBeOn = false;
                    notifyObserversWithError();
                    break;
            }
        });

        notifyObserversWithLocation();
    }

    private final static int REQUEST_CHECK_SETTINGS = 1000;
    boolean gpsShouldBeOn = false;

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    gpsShouldBeOn = true;
                    notifyObserversWithLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    gpsShouldBeOn = false;
                    notifyObserversWithError();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "Gps Error";

    boolean resolvingError = false;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (resolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                resolvingError = true;
                connectionResult.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                googleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            resolvingError = true;
        }
    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(activity.getFragmentManager(), "errordialog");
    }
}