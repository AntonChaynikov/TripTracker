package com.antonchaynikov.triptracker.data;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

class PlatformLocationSource implements LocationSource, LocationListener {

    private volatile static LocationSource sInstance;
    private LocationManager mLocationManager;
    private PublishSubject<Location> mLocationsBroadcast;
    private Filter<Location> mFilter;
    private boolean mIsLocationsUpdateEnabled;

    private PlatformLocationSource(LocationManager locationManager, Filter<Location> filter) {
        mLocationsBroadcast = PublishSubject.create();
        mLocationManager = locationManager;
        mFilter = (filter == null)? (location -> true) : filter;
    }

    static LocationSource getLocationSource(@NonNull LocationManager locationManager, @Nullable Filter<Location> filter) {
        if (sInstance == null) {
            synchronized (PlatformLocationSource.class) {
                if (sInstance == null) {
                    sInstance = new PlatformLocationSource(locationManager, filter);
                }
            }
        }
        return sInstance;
    }

    @VisibleForTesting
    void resetInstance() {
        sInstance = null;
    }

    @Override
    public void startUpdates() throws SecurityException {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        mIsLocationsUpdateEnabled = true;
    }

    @Override
    public void stopUpdates() {
        mLocationManager.removeUpdates(this);
        mIsLocationsUpdateEnabled = false;

    }

    @Override
    public boolean isUpdateAvailable() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public boolean isLocationsUpdateEnabled() {
        return mIsLocationsUpdateEnabled;
    }

    @NonNull
    @Override
    public Observable<Location> getLocationUpdates() {
        return mLocationsBroadcast;
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
