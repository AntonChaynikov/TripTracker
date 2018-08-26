package com.antonchaynikov.triptracker.MapActivity;

import android.location.Location;

public class PreciseLocationUpdatePolicy implements LocationUpdatePolicy {

    private Location mCurrentPreciseLocation;
    private Location mLastLocationUpdate;
    private long mLocationLifeTime;
    private long mTimeOfLastUpdate;
    private LocationQueue mLastLocationsRecieved;

    public PreciseLocationUpdatePolicy(long maxLocationLifeTime, int maxLocationCapacity) {
        mLocationLifeTime = maxLocationLifeTime;
        mLastLocationsRecieved = new LocationQueue(maxLocationCapacity);
    }

    @Override
    public Location getRelevantLocation() {
        if (mCurrentPreciseLocation == null) {
            updatePreciseLocation(mLastLocationUpdate);
        } else {
            if (isCurrentPreciseLocationTooOld()) {
                mCurrentPreciseLocation = mLastLocationsRecieved.getTheMostPreciseLocation();
            } else if (mLastLocationUpdate.getAccuracy() < mCurrentPreciseLocation.getAccuracy()){
                updatePreciseLocation(mLastLocationUpdate);
            }
        }
        return mCurrentPreciseLocation;
    }

    @Override
    public void updateLastLocationRecieved(Location location) {
        mLastLocationsRecieved.update(location);
        mLastLocationUpdate = location;
    }

    private void updatePreciseLocation(Location currentLocation) {
        mCurrentPreciseLocation = currentLocation;
        mTimeOfLastUpdate = System.currentTimeMillis();
    }

    private boolean isCurrentPreciseLocationTooOld() {
        return System.currentTimeMillis() - mTimeOfLastUpdate > mLocationLifeTime;
    }
}
