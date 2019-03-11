package com.antonchaynikov.triptracker.data.tripmanager;

import android.location.Location;
import android.util.Log;

import com.antonchaynikov.triptracker.data.model.TripCoordinate;

import androidx.annotation.NonNull;

public class StatisticsCalculator {

    private Location mPrevLocation;
    private double mDistance;
    private long mFirstLocationReceivedTime;

    private void updateStatistics(Location coordinate) {
        mDistance += mPrevLocation.distanceTo(coordinate);
        Log.d("StatisticsCalculator" , "Updating distance to" + mDistance);
    }

    void addCoordinate(@NonNull Location coordinate) {
        if (mPrevLocation != null) {
            updateStatistics(coordinate);
        } else {
            mFirstLocationReceivedTime = coordinate.getTime();
        }
        mPrevLocation = coordinate;
    }

    void addCoordinate(@NonNull TripCoordinate coordinate) {
        Location location = new Location("");
        location.setLatitude(coordinate.getLatitude());
        location.setLongitude(coordinate.getLongitude());
        addCoordinate(location);
    }

    double getDistance() {
        return mDistance;
    }

    double getSpeed() {
        double tripDurationSeconds = (mPrevLocation.getTime() - mFirstLocationReceivedTime) / 1000;
        return mDistance / tripDurationSeconds;
    }

    void reset() {
        mDistance = 0;
        mPrevLocation = null;
    }
}
