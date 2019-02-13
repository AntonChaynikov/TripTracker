package com.antonchaynikov.triptracker.data.location;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class FusedLocationService extends LocationService {

    private FusedLocationProviderClient mLocationClient;
    private LocationListener mLocationListener;
    private PublishSubject<Location> mLocationsBroadcast;
    private Filter<Location> mFilter;

    private boolean mIsReceivingLocations;
    private boolean mIsLocationAvailable;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationListener = new LocationListener();
        mIsLocationAvailable = true;
        mLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mLocationsBroadcast = PublishSubject.create();

    }

    public void startUpdates(@NonNull Filter<Location> locationFilter) throws SecurityException {
        if (!mIsReceivingLocations) {
            mLocationClient.requestLocationUpdates(getLocationRequest(), mLocationListener, null );
            mFilter = locationFilter;
            mIsReceivingLocations = true;
        }
    }

    public void stopUpdates() {
        mLocationClient.removeLocationUpdates(mLocationListener);
        mIsReceivingLocations = false;
    }

    public Observable<Location> getLocationsStream() {
        return mLocationsBroadcast;
    }

    public boolean isUpdateAvailable() {
        return mIsLocationAvailable;
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private class LocationListener extends LocationCallback {

        public void onLocationResult(LocationResult result) {
            for (Location location: result.getLocations()) {
                if (mFilter.isRelevant(location)) {
                    mLocationsBroadcast.onNext(location);
                }
            }
        }

        public void onLocationAvailability(LocationAvailability availability) {
            mIsLocationAvailable = availability.isLocationAvailable();
        }
    }
}
