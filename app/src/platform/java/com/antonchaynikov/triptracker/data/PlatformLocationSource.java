package com.antonchaynikov.triptracker.data;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

class PlatformLocationSource implements TripSource, ServiceConnection {

    private volatile static PlatformLocationSource sInstance;
    private PublishSubject<Location> mLocationsBroadcast;
    private boolean mIsLocationsUpdateEnabled;
    private LocationService mLocationService;
    private Filter<Location> mLocationFilter;
    private Trip mCurrentTrip;

    private PlatformLocationSource(@NonNull Filter<Location> filter) {
        mLocationsBroadcast = PublishSubject.create();
        mLocationFilter = filter;
    }

    static PlatformLocationSource getLocationSource(@NonNull Filter<Location> filter) {
        if (sInstance == null) {
            synchronized (PlatformLocationSource.class) {
                if (sInstance == null) {
                    sInstance = new PlatformLocationSource(filter);
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
    public void startTrip() throws SecurityException {
        if (isLocationsUpdateAvailable() && !mIsLocationsUpdateEnabled) {
            mCurrentTrip = Trip.beginNewTrip();
            mLocationService.startUpdates(mLocationFilter)
                    .doOnNext(mCurrentTrip::addLocation)
                    .subscribe(mLocationsBroadcast);
            mIsLocationsUpdateEnabled = true;
        }
    }

    @Override
    public Trip finishTrip() {
        mIsLocationsUpdateEnabled = false;
        if (mLocationService != null) {
            mLocationService.stopUpdates();
        }
        mLocationFilter.reset();
        return mCurrentTrip;
    }

    @Override
    public boolean isLocationsUpdateAvailable() {
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
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mLocationService = null;
    }
}