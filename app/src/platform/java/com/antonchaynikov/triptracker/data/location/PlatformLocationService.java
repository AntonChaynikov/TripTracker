package com.antonchaynikov.triptracker.data.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

public class PlatformLocationService extends LocationService implements LocationListener {

    private LocationManager mLocationManager;
    private Filter<Location> mFilter;

    private boolean mIsReceivingLocations;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mIsLocationAvailable = true;
    }

    @Override
    public void startUpdates(@NonNull Filter<Location> locationFilter) throws SecurityException {
        if (!mIsReceivingLocations) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            mFilter = locationFilter;
            mIsReceivingLocations = true;
        }
    }

    @Override
    public void stopUpdates() {
        mLocationManager.removeUpdates(this);
        mIsReceivingLocations = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mFilter.isRelevant(location)) {
            mLocationsBroadcast.onNext(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
