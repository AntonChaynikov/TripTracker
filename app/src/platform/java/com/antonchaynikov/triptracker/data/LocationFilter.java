package com.antonchaynikov.triptracker.data;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

public class LocationFilter implements Filter<Location> {

    public static final float DEFAULT_ACCURACY = 15;
    public static final float DEFAULT_VELOCITY_LIMIT = 30; // km/h
    public static final float DEFAULT_DISTANCE_MARGIN = 15; // m

    private static String TAG = LocationFilter.class.getSimpleName();

    private double mAccuracyMargin;
    private double mVelocityLimit;
    private double mDistanceMargin;

    private Location mLastLocationReceived;

    public LocationFilter() {
        mAccuracyMargin = DEFAULT_ACCURACY;
        mVelocityLimit = DEFAULT_VELOCITY_LIMIT;
        mDistanceMargin = DEFAULT_DISTANCE_MARGIN;
    }

    public LocationFilter(float accuracy, float velocityLimit, float distanceMargin) {
        mAccuracyMargin = accuracy;
        mVelocityLimit = velocityLimit;
        mDistanceMargin = distanceMargin;
    }

    @Override
    public boolean isRelevant(@NonNull Location location) {
        if (location.getAccuracy() > mAccuracyMargin) {
            Log.d(TAG, "Accuracy is wrong expected < " + mAccuracyMargin +" received " + location.getAccuracy());
            return false;
        }
        if (mLastLocationReceived == null) {
            mLastLocationReceived = location;
            return true;
        }
        if (location.getTime() < mLastLocationReceived.getTime()) {
            Log.d(TAG, "The time is wrong");
            return false;
        }
        if (calcSpeed(location) > mVelocityLimit) {
            Log.d(TAG, "The speed is wrong");
            return false;
        }
        if (mLastLocationReceived.distanceTo(location) < mDistanceMargin) {
            Log.d(TAG, "The distance is wrong expected > " + mDistanceMargin +" received " + mLastLocationReceived.distanceTo(location));
            return false;
        }
        mLastLocationReceived = location;
        return true;
    }

    @VisibleForTesting
    float calcSpeed(@NonNull Location location) {
        float elapsedSeconds = (location.getTime() - mLastLocationReceived.getTime()) / 1000f;
        float distance = mLastLocationReceived.distanceTo(location);
        return distance / 1000f / (elapsedSeconds / 60 / 60);
    }

    public void reset() {
        mLastLocationReceived = null;
    }

}