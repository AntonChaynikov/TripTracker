package com.antonchaynikov.triptracker.data;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;

public class LocationService extends Service implements LocationListener {

    private final IBinder mBinder = new LocalServiceBinder();
    private LocationManager mLocationManager;
    private ReplaySubject<Location> mLocationsBroadcast;
    private Filter<Location> mFilter;

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
        mLocationsBroadcast = ReplaySubject.create();
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onDestroy() {

    }

    public void startUpdates(@NonNull Filter<Location> locationFilter) throws SecurityException {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        mFilter = locationFilter;
    }

    public void stopUpdates() {
        mLocationManager.removeUpdates(this);
    }

    public boolean isUpdateAvailable() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @NonNull
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
