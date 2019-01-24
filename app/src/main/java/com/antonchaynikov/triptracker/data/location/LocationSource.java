package com.antonchaynikov.triptracker.data.location;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class LocationSource implements ServiceConnection {

    private static volatile LocationSource sInstance;

    private PublishSubject<Location> mLocationsBroadcast;
    private LocationService mLocationService;
    private Filter<Location> mLocationFilter;
    private Disposable mLocationsStreamSubscription;

    private boolean mIsLocationsUpdateEnabled;

    private boolean mWaitingForService;

    private LocationSource(@NonNull Filter<Location> filter) {
        mLocationsBroadcast = PublishSubject.create();
        mLocationFilter = filter;
    }

    public static LocationSource getInstance(@NonNull Filter<Location> filter) {
        if (sInstance == null) {
            synchronized (LocationSource.class) {
                if (sInstance == null) {
                    sInstance = new LocationSource(filter);
                }
            }
        }
        return sInstance;
    }

    @VisibleForTesting
    void resetInstance() {
        sInstance = null;
    }

    public void startUpdates() throws SecurityException {
        if (isLocationsUpdateAvailable() && !mIsLocationsUpdateEnabled) {
            mLocationService.startUpdates(mLocationFilter);
            mLocationsStreamSubscription = mLocationService.getLocationsStream().subscribe(mLocationsBroadcast::onNext);
            mIsLocationsUpdateEnabled = true;
        } else {
            mWaitingForService = true;
        }
    }

    public void finishUpdates() {
        mIsLocationsUpdateEnabled = false;
        if (mLocationService != null) {
            mLocationService.stopUpdates();
        }
        mLocationFilter.reset();
        if (mLocationsStreamSubscription != null) {
            mLocationsStreamSubscription.dispose();
        }
    }

    public boolean isLocationsUpdateAvailable() {
        return mLocationService != null && mLocationService.isUpdateAvailable();
    }

    public boolean isLocationsUpdateEnabled() {
        return mIsLocationsUpdateEnabled;
    }

    @NonNull
    public Observable<Location> getLocationUpdates() {
        return mLocationsBroadcast;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mLocationService = ((LocationService.LocationServiceBinder)service).getLocationService();
        if (mWaitingForService) {
            mWaitingForService = false;
            startUpdates();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mLocationService = null;
    }
}
