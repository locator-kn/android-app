package com.locator_app.locator.util;

import com.google.android.gms.maps.model.LatLng;
import com.locator_app.locator.model.GeoTag;

public class DistanceCalculator {

    public static double distanceInKm(GeoTag geoTag, LatLng latLng) {
        return distanceInKm(geoTag.getLatitude(), geoTag.getLongitude(),
                            latLng.latitude, latLng.longitude);
    }

    public static double distanceInKm(GeoTag geoTag1, GeoTag geoTag2) {
        return distanceInKm(geoTag1.getLatitude(), geoTag1.getLongitude(),
                            geoTag2.getLatitude(), geoTag2.getLongitude());
    }

    public static double distanceInKm(LatLng latLng1, LatLng latLng2) {
        return distanceInKm(latLng1.latitude, latLng1.longitude,
                            latLng2.latitude, latLng2.longitude);
    }

    public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        final double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float distInMeters = (float) (earthRadius * c);
        return distInMeters / 1000;
    }

}
