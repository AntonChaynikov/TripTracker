package com.antonchaynikov.triptracker.data;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class LocationService extends Service implements LocationListener {

    private final IBinder mBinder = new LocalServiceBinder();
    private LocationManager mLocationManager;
    private PublishSubject<Location> mLocationsBroadcast;
    private Filter<Location> mFilter;

    private boolean mIsReceivingLocations;

    public class LocalServiceBinder extends Binder {
        public LocationService getLocationService() {
            return LocationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Locations update")
                    .setContentText("Running background service to get geolocation data").build();

            startForeground(1, notification);
        }

        mLocationsBroadcast = PublishSubject.create();
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        stopUpdates();
    }

    public void startUpdates(@NonNull Filter<Location> locationFilter) throws SecurityException {
        if (!mIsReceivingLocations) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            mFilter = locationFilter;
            mIsReceivingLocations = true;
        }
    }

    public void stopUpdates() {
        mLocationManager.removeUpdates(this);
        mIsReceivingLocations = false;
    }

    public Observable<Location> getLocationsStream() {
        return mLocationsBroadcast;
    }

    public boolean isUpdateAvailable() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
