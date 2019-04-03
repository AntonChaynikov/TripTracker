package com.antonchaynikov.triptracker.data.location;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.TripActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class LocationService extends Service implements LocationConsumer {

    private static final String TAG = LocationService.class.getCanonicalName();

    private static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 1;

    private PublishSubject<Location> mLocationsObservable;
    private BehaviorSubject<Boolean> mGeolocationAvailabilityUpdatesObservable;

    private LocationProvider mLocationProvider;
    private boolean mIsReceivingLocations;
    private boolean mIsLocationAvailable;

    private final IBinder mBinder = new LocationService.LocationServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    getString(R.string.notification_location_service),
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_location_service))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(TripActivity.getNotificationContentIntent(getApplicationContext()))
                .build();

        startForeground(NOTIFICATION_ID, notification);

        mIsLocationAvailable = true;
        mLocationsObservable = PublishSubject.create();
        mGeolocationAvailabilityUpdatesObservable = BehaviorSubject.create();
    }

    @Override
    public void onDestroy() {
        if (mIsReceivingLocations) {
            stopUpdates();
        }
    }

    void setLocationProvider(@NonNull LocationProvider locationProvider) {
        mLocationProvider = locationProvider;
    }

    public Observable<Location> getLocationsStream() {
        return mLocationsObservable;
    }

    public Observable<Boolean> getGeolocationAvailabilityUpdatesObservable() {
        return mGeolocationAvailabilityUpdatesObservable;
    }

    public void startUpdates() {
        Log.d(TAG, "LocationService start updates");
        if (!mIsReceivingLocations && mLocationProvider != null) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                mLocationProvider.startUpdates(this);
                mIsReceivingLocations = true;
            } else {
                throw new IllegalStateException("Should have asked geo permission");
            }
        } else if (mLocationProvider == null) {
            Log.e(TAG, "LocationProvider is null");
            mGeolocationAvailabilityUpdatesObservable.onNext(false);
        }
    }

    public void stopUpdates() {
        mIsReceivingLocations = false;
        if (mLocationProvider != null) {
            mLocationProvider.stopUpdates(this);
        }
    }

    @Override
    public void onNewLocationUpdate(@NonNull Location location) {
        mLocationsObservable.onNext(location);
    }

    @Override
    public void onLocationUpdatesAvailabilityChange(boolean areUpdatesAvailable) {
        boolean isStatusChanging = mIsLocationAvailable ^ areUpdatesAvailable;
        if (isStatusChanging) {
            mIsLocationAvailable = areUpdatesAvailable;
            mGeolocationAvailabilityUpdatesObservable.onNext(areUpdatesAvailable);
        }
    }

    class LocationServiceBinder extends Binder {
        LocationService getLocationService() {
            return LocationService.this;
        }
    }
}
