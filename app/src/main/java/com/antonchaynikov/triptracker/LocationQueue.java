package com.antonchaynikov.triptracker;

import android.location.Location;
import android.util.Log;

import java.util.LinkedList;

public class LocationQueue {

    private final static String TAG = "LocationQueue";

    private int mMaxQueuCapacity;
    private LinkedList<Location> mLocationsList = new LinkedList<>();

    public LocationQueue(int maxCapacity) {
        mMaxQueuCapacity = maxCapacity;
    }

    public void update(Location location) {
        if (mLocationsList.size() == mMaxQueuCapacity) {
            Log.d(TAG, "Removing " + mLocationsList.getFirst().toString());
            mLocationsList.pollFirst();
        }
        Log.d(TAG, "Adding " + location.toString());
        mLocationsList.add(location);
    }

    public Location getTheMostPreciseLocation() {
        Location mostPreciseLocation = null;
        for (Location currentLocation: mLocationsList) {
            if (mostPreciseLocation == null || isMoreRelevantThan(currentLocation, mostPreciseLocation) ) {
                mostPreciseLocation = currentLocation;
            }
        }
        return mostPreciseLocation;
    }

    private boolean isMoreRelevantThan(Location l1, Location l2) {
        if (l1.hasAccuracy() && l2.hasAccuracy()) {
            return l1.getAccuracy() < l2.getAccuracy();
        }
        boolean isMoreRecent = l1.getElapsedRealtimeNanos() < l2.getElapsedRealtimeNanos();
        return l1.hasAccuracy() || isMoreRecent;
    }
}
