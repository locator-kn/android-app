package com.locator_app.locator.util;


import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.locator_app.locator.LocatorApplication;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class GpsService extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;

    boolean connected = false;

    public GpsService() {
        googleApiClient = new GoogleApiClient.Builder(LocatorApplication.getAppContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    List<Observer> firstCurLocationSubscribers = new LinkedList<>();
    Observable<Location> firstCurLocation = Observable.create(firstCurLocationSubscribers::add);

    public void getCurLocationOnGUIThread(Action1<? super Location> returnFunction) {
        getCurLocationObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(returnFunction);
    }

    public void getCurLocationOnIOThread(Action1<? super Location> returnFunction) {
        getCurLocationObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(returnFunction,
                           (error) -> {});
    }

    public Observable<android.location.Location> getCurLocationObservable() {
        if (connected) {
            android.location.Location location = googlePlayCurLocation();
            if (location != null)
            {
                return Observable.just(location);
            }
        }
        return firstCurLocation;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        connected = true;

        android.location.Location location = googlePlayCurLocation();
        if (location != null) {
            for (Observer subscriber : firstCurLocationSubscribers) {
                subscriber.onNext(location);
                subscriber.onCompleted();
            }
        } else {
            for (Observer subscriber : firstCurLocationSubscribers) {
                subscriber.onCompleted(); //TODO: convert to onError
            }
        }
    }

    private android.location.Location googlePlayCurLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
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
                connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
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
        dialogFragment.show(getActivity().getFragmentManager(), "errordialog");
    }

//    public Observable<android.location.Location> getContinuousCurLocation() {
//TODO: implement continuous Location updates
//    }

    //TODO: Dialog when GPS is off

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        googleApiClient.connect();
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        connected = false;
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return new View(LocatorApplication.getAppContext());
    }
}