package com.antonchaynikov.triptracker.data.location;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public final class LocationSource implements ServiceConnection {

    private static final String TAG  = LocationSource.class.getCanonicalName();

    @SuppressLint("StaticFieldLeak")
    private static volatile LocationSource sInstance;

    private Context mAppContext;
    private LocationService mLocationService;

    private PublishSubject<Location> mLocationObservable;
    private BehaviorSubject<Boolean> mGeolocationAvailabilityObservable;
    private CompositeDisposable mSubscriptions = new CompositeDisposable();
    private LocationProvider mLocationProvider;

    private PublishSubject<LocationService> mServiceConnectionObservable;

    private boolean mIsServiceRunning;
    private boolean mIsTestMode;

    // Used for testing purposes to notify when the location service has been connected
    private CountDownLatch mCountDownLatch;

    private LocationSource(@NonNull Context context) {
        mAppContext = context.getApplicationContext();
        mLocationObservable = PublishSubject.create();
        mServiceConnectionObservable = PublishSubject.create();
        mGeolocationAvailabilityObservable = BehaviorSubject.create();
    }

    public static LocationSource getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (LocationSource.class) {
                if (sInstance == null) {
                    sInstance = new LocationSource(context);
                }
            }
        }
        return sInstance;
    }

    @VisibleForTesting
    public void setServiceConnectedSyncMode() throws InterruptedException {
        mIsTestMode = true;
        mCountDownLatch = new CountDownLatch(1);
    }

    public void setLocationProvider(@Nullable LocationProvider locationProvider) {
        if (mIsServiceRunning) {
            throw new IllegalStateException("Should've stopped current LocationProvider first");
        }
        mLocationProvider = locationProvider;
    }

    public void startUpdates() {
        Log.d(TAG, "startUpdates");
        Log.d(TAG, "Thread " + Thread.currentThread().getId());
        if (mLocationProvider != null) {
            startService();

            Observable.just(true).zipWith(mServiceConnectionObservable, (event, service) -> service)
                    .subscribe(service -> {
                        Log.d(TAG, "Service connected");
                        mLocationService = service;
                        mLocationService.setLocationProvider(mLocationProvider);
                        mLocationService.startUpdates();
                    });

            testModeWaitForServiceConnection(mIsTestMode);

        } else {
            Log.e(TAG, "LocationProvider is null");
            mGeolocationAvailabilityObservable.onNext(false);
        }
    }

    private void testModeWaitForServiceConnection(boolean isTestMode) {
        if (isTestMode) {
            while (mCountDownLatch.getCount() > 0) {
                try {
                    mCountDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(mAppContext, LocationService.class);
        if (!mIsServiceRunning) {
            ActivityCompat.startForegroundService(mAppContext, serviceIntent);
        }
        mIsServiceRunning = mAppContext.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
        if (!mIsServiceRunning) {
            mGeolocationAvailabilityObservable.onNext(false);

            if (mIsTestMode) {
                mCountDownLatch.countDown();
            }
        }
    }

    public void finishUpdates() {
        Log.d(TAG, "finishUpdates");
        mLocationService.stopUpdates();
        mAppContext.unbindService(this);
        mAppContext.stopService(new Intent(mAppContext, LocationService.class));
        mIsServiceRunning = false;
    }

    public Observable<Location> getLocationsObservable() {
        return mLocationObservable;
    }

    public Observable<Boolean> getGeolocationAvailabilityObservable() {
        return mGeolocationAvailabilityObservable;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        LocationService service = ((LocationService.LocationServiceBinder) iBinder).getLocationService();
        mServiceConnectionObservable.onNext(service);
        mSubscriptions.add(service
                .getLocationsStream()
                .subscribe(mLocationObservable::onNext));
        mSubscriptions.add(service
                .getGeolocationAvailabilityUpdatesObservable()
                .subscribe(mGeolocationAvailabilityObservable::onNext));

        if (mIsTestMode) {
            mCountDownLatch.countDown();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mSubscriptions.dispose();
        mIsServiceRunning = false;
    }
}
