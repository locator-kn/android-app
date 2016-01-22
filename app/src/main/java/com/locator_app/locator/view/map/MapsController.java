package com.locator_app.locator.view.map;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.model.LocatorLocation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MapsController {

    HashSet<LocatorLocation> drawnlocations = new HashSet<>();
    Queue<LocatorLocation> newLocations = new LinkedList<>();

    MapsActivity mapsActivity;

    public MapsController(MapsActivity maps) {
        mapsActivity = maps;
    }

    private final static int MIN_CALL_DELAY_MS = 2000;
    private long lastCall;
    public boolean calledRecently() {
        long delay = System.currentTimeMillis() - lastCall;
        lastCall = System.currentTimeMillis();
        return delay < MIN_CALL_DELAY_MS;
    }

    public void drawLocationsAt(LatLng position) {
        if (!calledRecently()) {
//                drawHeatMapAt(cameraPosition.target);
            drawLocationsAtP(position);
        }
    }

    private void drawLocationsAtP(LatLng position) {
        LocationController.getInstance().getLocationsNearby(position.longitude, position.latitude, 1, 100)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (item) -> newLocations.add(item),
                        (error) -> Toast.makeText(LocatorApplication.getAppContext(),
                                "Fehler beim Laden von Locations",
                                Toast.LENGTH_SHORT),
                        this::drawNewLocations
                );
    }

    public void drawNewLocations() {
        while (!newLocations.isEmpty()) {
            LocatorLocation location = newLocations.poll();
            if (drawnlocations.contains(location)) {
                continue;
            }
            mapsActivity.drawLocation(location.geoTag.getLongitude(), location.geoTag.getLatitude());
            drawnlocations.add(location);
        }
    }

    private List<LatLng> heatPoints;

    public void addHeatMap(double lon, double lat) {
        heatPoints = new LinkedList<>();

        SchoenHierController.getInstance().schoenHiersNearby(lon, lat, 10, 100)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(response -> response.results)
                .subscribe(
                        (item) -> {
                            double shLon = item.schoenHier.geoTag.getLongitude();
                            double shLat = item.schoenHier.geoTag.getLatitude();

                            heatPoints.add(new LatLng(shLat, shLon));
                        },
                        (error) -> Toast.makeText(LocatorApplication.getAppContext(),
                                "Schön hier nicht bekommen",
                                Toast.LENGTH_SHORT),
                        () -> mapsActivity.drawHeatMap(heatPoints)
                );
    }
}
