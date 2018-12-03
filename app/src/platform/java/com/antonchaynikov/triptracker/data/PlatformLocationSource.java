package com.antonchaynikov.triptracker.data;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

class PlatformLocationSource implements LocationSource, ServiceConnection {

    private volatile static PlatformLocationSource sInstance;
    private PublishSubject<Location> mLocationsBroadcast;
    private boolean mIsLocationsUpdateEnabled;
    private LocationService mLocationService;

    private PlatformLocationSource() {
        mLocationsBroadcast = PublishSubject.create();
    }

    static PlatformLocationSource getLocationSource() {
        if (sInstance == null) {
            synchronized (PlatformLocationSource.class) {
                if (sInstance == null) {
                    sInstance = new PlatformLocationSource();
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
        mIsLocationsUpdateEnabled = true;
        if (isUpdateAvailable()) {
            mLocationService.startUpdates(new LocationFilter());
        }
    }

    @Override
    public void stopUpdates() {
        mIsLocationsUpdateEnabled = false;
        if (mLocationService != null) {
            mLocationService.stopUpdates();
        }
    }

    @Override
    public boolean isUpdateAvailable() {
        return mLocationService != null && mLocationService.isUpdateAvailable();
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
    public void onServiceConnected(ComponentName name, IBinder service) {
        mLocationService = ((LocationService.LocalServiceBinder)service).getLocationService();
        mLocationService
                .getLocationUpdates()
                .subscribe(mLocationsBroadcast);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mLocationService = null;
    }
}
