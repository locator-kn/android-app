package com.locator_app.locator.view.map;


import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class HideClusterRenderer extends DefaultClusterRenderer<LocationMarker> {

    public HideClusterRenderer(Context context, GoogleMap map, ClusterManager<LocationMarker> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<LocationMarker> cluster)
    {
        return false;
    }
}
