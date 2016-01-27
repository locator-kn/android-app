package com.locator_app.locator.view.map;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class LocationMarker implements ClusterItem {
    private final LatLng position;
    private BitmapDescriptor icon;

    public LocationMarker(double lat, double lng, BitmapDescriptor locationIcon) {
        position = new LatLng(lat, lng);
        icon = locationIcon;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }
}
