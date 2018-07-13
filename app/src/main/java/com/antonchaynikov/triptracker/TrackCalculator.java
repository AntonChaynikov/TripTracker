package com.antonchaynikov.triptracker;

import android.location.Location;

import java.util.Queue;

public class TrackCalculator {

    private final static int MIN_DISTANCE_THRESHOLD_METERS = 60;
    private final static float DEFAULT_ACCURACY = 100;

    private double mAverageAccuracy = Double.NaN;

    private Queue<Location> mLocations;

    public void addLocation(Location location) {
         updateAverageAccuracy(location);
    }

    private void updateAverageAccuracy(Location location) {
        float accuracy;
        if (!location.hasAccuracy()) {
            accuracy = DEFAULT_ACCURACY;
        } else {
            accuracy = location.getAccuracy();
        }
        if (mAverageAccuracy == Double.NaN) {
            mAverageAccuracy = accuracy;
        } else {
            mAverageAccuracy = (mAverageAccuracy + accuracy) / 2;
        }
    }

    private float getAccuracy(Location location) {
        return location.hasAccuracy()? location.getAccuracy() : DEFAULT_ACCURACY;
    }

    private boolean isLikelyMoving(Location location) {
        // TODO complete method
        return false;
    }
}
