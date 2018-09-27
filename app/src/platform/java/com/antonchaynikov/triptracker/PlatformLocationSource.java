package com.antonchaynikov.triptracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.antonchaynikov.triptracker.MapActivity.LocationSource;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
  *  Uses platform location API in android.location to provide location
  *  instead of more high level Google Location Services API
  */
public class PlatformLocationSource implements LocationSource, LocationListener {

    private static volatile PlatformLocationSource instance;

    private final static String TAG = "PlatformLocationSource";
    private final static long GPS_UPDATE_INTERVAL = 1000 * 10;
    private final static long NETWORK_UPDATE_INTERVAL = 1000;
    private final static float MIN_DISTANCE_TO_UPDATE = 0;

    private LocationManager mLocationManager;
    private BehaviorSubject<Location> mLocationBroadcast = BehaviorSubject.create();
    private boolean mUpdating;

    public static PlatformLocationSource getInstance(Context context) {
        Log.d(TAG, "Using as a LocationSource");
        if (instance == null) {
            synchronized (PlatformLocationSource.class) {
                if (instance == null) {
                    instance = new PlatformLocationSource(context);
                }
            }
        }
        return instance;
    }

    private PlatformLocationSource(Context context) {
        mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void startUpdates() {
        if (!isUpdateEnabled()) {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_INTERVAL, MIN_DISTANCE_TO_UPDATE, this);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, NETWORK_UPDATE_INTERVAL, MIN_DISTANCE_TO_UPDATE, this);
                mUpdating = true;
            } catch (SecurityException e) {
                Log.e(TAG, "Failed to get location. No permission granted" + e);
            }
        }
    }

    @Override
    public void stopUpdates() {
        if (isUpdateEnabled()) {
            mLocationManager.removeUpdates(this);
            mUpdating = false;
        }
    }

    @Override
    public boolean isUpdateEnabled() {
        return mUpdating;
    }

    @Override
    public Observable<Location> getLocationUpdates() {
        return mLocationBroadcast;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationBroadcast.onNext(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "Provider enabled " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "Provider disabled " + s);
    }

}
