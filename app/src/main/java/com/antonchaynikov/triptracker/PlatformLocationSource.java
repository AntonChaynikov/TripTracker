package com.antonchaynikov.triptracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
  *  Uses platform location API in android.location to provide location
  *  instead of more high level Google Location Services API
  */
public class PlatformLocationSource implements LocationSource, LocationListener {

    private static volatile PlatformLocationSource instance;

    private final static String TAG = "PlatformLocationSource";
    private final static long GPS_UPDATE_INTERVAL = 100;
    private final static long NETWORK_UPDATE_INTERVAL = 100;
    private final static float MIN_DISTANCE_TO_UPDATE = 0;

    private ObservableEmitter<Location> mEmitter;

    private LocationManager mLocationManager;
    private LocationUpdatePolicy mLocationUpdatePolicy;
    private Observable<Location> mLocationBroadcast;
    private boolean mUpdating;

    public static PlatformLocationSource getInstance(Context context, LocationUpdatePolicy locationUpdatePolicy) {
        if (instance == null) {
            synchronized (PlatformLocationSource.class) {
                if (instance == null) {
                    instance = new PlatformLocationSource(context, locationUpdatePolicy);
                }
            }
        }
        return instance;
    }

    private PlatformLocationSource(Context context, LocationUpdatePolicy locationUpdatePolicy) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationUpdatePolicy = locationUpdatePolicy;
    }

    @Override
    public void toggleLocationUpdates() {
        if (isUpdateEnabled()) {
            mLocationManager.removeUpdates(this);
            mUpdating = false;
            Log.d(TAG, "Stopped updating");
        } else {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, NETWORK_UPDATE_INTERVAL, MIN_DISTANCE_TO_UPDATE, this);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_INTERVAL, MIN_DISTANCE_TO_UPDATE, this);
                mUpdating = true;
                Log.d(TAG, "Started updating");
            } catch (SecurityException e) {
                Log.e(TAG, "Failed to get location. No permission granted" + e);
            }
        }
    }

    @Override
    public boolean isUpdateEnabled() {
        return mUpdating;
    }

    @Override
    public Observable<Location> getLocation() {
        mLocationBroadcast = Observable.create(new ObservableOnSubscribe<Location>() {
            @Override
            public void subscribe(ObservableEmitter<Location> emitter) throws Exception {
                mEmitter = emitter;
            }
        });
        return mLocationBroadcast;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationUpdatePolicy.updateLastLocationRecieved(location);
        Location relevantLoc = mLocationUpdatePolicy.getRelevantLocation();
        Log.d(TAG, "Location received " + location.toString());
        Log.d(TAG, "Last RelevantLocation is  " + relevantLoc.toString());
        mEmitter.onNext(relevantLoc);
    }

    @Override
    public void onStatusChanged(String provider, int statusCode, Bundle bundle) {
        String status;
        switch (statusCode) {
            case LocationProvider.OUT_OF_SERVICE : {
                status = "OUT_OF_SERVICE";
                break;
            }
            case LocationProvider.TEMPORARILY_UNAVAILABLE : {
                status = "TEMPORARILY_UNAVAILABLE";
                break;
            }
            case LocationProvider.AVAILABLE : {
                status = "AVAILABLE";
                break;
            }
            default: {
                status = "undefined";
            }
        }
        Log.d(TAG, "Provider - " + provider + " Status -  " + status);
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "Provider - " + provider + " disabled ");
    }

}
