package com.locator_app.locator.view.map;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Map;

public class LocationMarkerRenderer extends DefaultClusterRenderer<LocationMarker> {

    private Map<String, LocationMarker> markerIDToLocationMarker;

    public LocationMarkerRenderer(Context context, GoogleMap map,
                                  ClusterManager<LocationMarker> clusterManager,
                                  Map<String, LocationMarker> markerToLocationMarkerMap) {
        super(context, map, clusterManager);
        markerIDToLocationMarker = markerToLocationMarkerMap;
    }

    @Override
    protected void onBeforeClusterItemRendered(LocationMarker item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getIcon());
//        markerOptions.snippet(item.getSnippet());
//        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected void onClusterItemRendered(LocationMarker person, Marker marker) {
        super.onClusterItemRendered(person, marker);
        markerIDToLocationMarker.put(marker.getId(), person);
    }
}
