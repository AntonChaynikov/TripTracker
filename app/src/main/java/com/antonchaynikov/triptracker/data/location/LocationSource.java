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

public final class LocationSource implements ServiceConnection {

    private static volatile LocationSource sInstance;

    private PublishSubject<Location> mLocationsBroadcast;
    private PublishSubject<Boolean> mGeolocationAvailabilityBroadcast;
    private PublishSubject<IBinder> mServiceConnectionEvent;
    private ServiceManager<?> mServiceManager;
    private LocationService mLocationService;
    private Filter<Location> mLocationFilter;

    private Disposable mUpdateRequestDisposable;

    private LocationSource(@NonNull Filter<Location> filter, @NonNull ServiceManager<?> serviceManager) {
        mLocationsBroadcast = PublishSubject.create();
        mServiceConnectionEvent = PublishSubject.create();
        mGeolocationAvailabilityBroadcast = PublishSubject.create();
        mLocationFilter = filter;
        mServiceManager = serviceManager;
    }

    public static LocationSource getInstance(@NonNull Filter<Location> filter, @NonNull ServiceManager<?> serviceManager) {
        if (sInstance == null) {
            synchronized (LocationSource.class) {
                if (sInstance == null) {
                    sInstance = new LocationSource(filter, serviceManager);
                }
            }
        }
        return sInstance;
    }

    @VisibleForTesting
    static void resetInstance() {
        sInstance = null;
    }

    public void startUpdates() throws SecurityException {
        mServiceManager.startLocationService(this);
        mUpdateRequestDisposable = Observable.just(true)
                .zipWith(mServiceConnectionEvent, (areUpdatesRequested, iBinder) -> iBinder)
                .subscribe(this::requestUpdates);
    }

    private void requestUpdates(@NonNull IBinder iBinder) {
        mLocationService = ((LocationService.LocationServiceBinder) iBinder).getLocationService();
        Disposable d = mLocationService.getLocationsStream().subscribe(mLocationsBroadcast::onNext);
        d = mLocationService.getGeolocationAvailabilityUpdatesBroadcast().subscribe(mGeolocationAvailabilityBroadcast::onNext);
        mLocationService.startUpdates(mLocationFilter);
    }

    public void finishUpdates() {
        if (mLocationService != null) {
            mLocationService.stopUpdates();
        }
        mServiceManager.stopLocationService(this);
        if (mUpdateRequestDisposable != null) {
            mUpdateRequestDisposable.dispose();
        }
    }

    @NonNull
    public Observable<Location> getLocationUpdates() {
        return mLocationsBroadcast;
    }

    @NonNull
    public Observable<Boolean> getGeolocationAvailabilityUpdates() {
        return mGeolocationAvailabilityBroadcast;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mServiceConnectionEvent.onNext(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mLocationService = null;
    }
}
