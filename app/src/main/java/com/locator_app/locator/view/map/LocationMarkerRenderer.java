package com.locator_app.locator.view.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class LocationMarkerRenderer extends DefaultClusterRenderer<LocationMarker> {

    public LocationMarkerRenderer(Context context, GoogleMap map,
                                  ClusterManager<LocationMarker> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(LocationMarker item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getIcon());
//        markerOptions.snippet(item.getSnippet());
//        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
