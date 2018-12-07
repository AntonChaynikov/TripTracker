package com.antonchaynikov.triptracker.data;

import android.location.Location;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;

public class Trip {

    private static final String TAG = Trip.class.getSimpleName();

    private List<Location> mLocationsList;
    private double mDistance;

    private Trip() {
        mLocationsList = new LinkedList<>();
    }

    public static Trip beginNewTrip() {
        return new Trip();
    }

    public void addLocation(@NonNull Location location) {
        int listSize = mLocationsList.size();
        if (listSize > 0) {
            mDistance += location.distanceTo(mLocationsList.get(listSize - 1));
        }
        Log.d(TAG, "A");
        mLocationsList.add(location);
    }

    public List<Location> getLocationsList() {
        return mLocationsList;
    }

    public double getDistance() {
        return mDistance;
    }
}
