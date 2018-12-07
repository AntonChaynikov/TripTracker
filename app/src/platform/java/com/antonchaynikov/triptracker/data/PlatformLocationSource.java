package com.antonchaynikov.triptracker.data;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

class PlatformLocationSource implements TripSource, ServiceConnection {

    private volatile static PlatformLocationSource sInstance;
    private ReplaySubject<Location> mLocationsBroadcast;
    private boolean mIsLocationsUpdateEnabled;
    private LocationService mLocationService;
    private Filter<Location> mLocationFilter;
    private Trip mCurrentTrip;
    private Disposable mLocationsStreamSubscription;

    private PlatformLocationSource(@NonNull Filter<Location> filter) {
        mLocationsBroadcast = ReplaySubject.create();
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
            mLocationService.startUpdates(mLocationFilter);
            mIsLocationsUpdateEnabled = true;
            mLocationsStreamSubscription = mLocationService.getLocationsStream().subscribe(this::onNewLocation);
        }
    }

    private void onNewLocation(@NonNull Location location) {
        mCurrentTrip.addLocation(location);
    }

    @Override
    public Trip finishTrip() {
        mIsLocationsUpdateEnabled = false;
        if (mLocationService != null) {
            mLocationService.stopUpdates();
        }
        mLocationFilter.reset();
        mLocationsStreamSubscription.dispose();
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
        mLocationsBroadcast = ReplaySubject.create();
        subscribeToLocationsService();
        if (mCurrentTrip != null) {
            return Observable
                    .fromIterable(mCurrentTrip.getLocationsList())
                    .concatWith(mLocationsBroadcast);
        }
        return mLocationsBroadcast;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mLocationService = ((LocationService.LocalServiceBinder)service).getLocationService();
        subscribeToLocationsService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mLocationService = null;
    }

    private void subscribeToLocationsService() {
        if (mLocationService != null) {
            mLocationService
                    .getLocationsStream()
                    .subscribe(mLocationsBroadcast);
        }
    }

}