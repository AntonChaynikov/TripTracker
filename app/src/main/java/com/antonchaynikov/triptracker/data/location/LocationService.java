package com.antonchaynikov.triptracker.data.location;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.mainscreen.TripActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public abstract class LocationService extends Service {

    private static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 1;

    protected PublishSubject<Location> mLocationsBroadcast;
    protected boolean mIsLocationAvailable;

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
        mLocationsBroadcast = PublishSubject.create();
    }

    @Override
    public void onDestroy() {
        stopUpdates();
    }

    public abstract void startUpdates(@NonNull Filter<Location> locationFilter) throws SecurityException;

    public abstract void stopUpdates();

    public Observable<Location> getLocationsStream() {
        return mLocationsBroadcast;
    }

    public boolean isUpdateAvailable() {
        return mIsLocationAvailable;
    }

    class LocationServiceBinder extends Binder {
        LocationService getLocationService() {
            return LocationService.this;
        }
    }
}
