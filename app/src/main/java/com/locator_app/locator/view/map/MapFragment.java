package com.locator_app.locator.view.map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapFragment extends SupportMapFragment {
    public View originalContentView;
    public TouchableWrapper touchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        originalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        touchView = new TouchableWrapper(getActivity());
        touchView.addView(originalContentView);
        return touchView;
    }

    @Override
    public View getView() {
        return originalContentView;
    }

    MapsController mapsController;
    GoogleMap googleMap;

    boolean isInitialized = false;
    public void init(MapsController controller, GoogleMap map) {
        mapsController = controller;
        googleMap = map;

        isInitialized = true;
    }

    public class TouchableWrapper extends FrameLayout {

        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && isInitialized) {
                LatLng curMapPos = googleMap.getCameraPosition().target;
                mapsController.drawLocationsAt(curMapPos);
                mapsController.drawHeatMapAt(curMapPos);
            }
            return super.dispatchTouchEvent(event);
        }
    }
}
