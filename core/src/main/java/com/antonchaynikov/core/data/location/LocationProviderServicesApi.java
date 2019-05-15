package com.antonchaynikov.core.data.location;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

//TODO use different providers depending on the build flavor
class LocationProviderServicesApi extends LocationCallback implements LocationProvider {

    private FusedLocationProviderClient mLocationClient;
    private LocationConsumer mConsumer;
    private Filter<Location> mFilter;

    LocationProviderServicesApi(@NonNull Context context) {
        mLocationClient = LocationServices.getFusedLocationProviderClient(context.getApplicationContext());
    }

    @Override
    public void startUpdates(@NonNull LocationConsumer consumer) throws SecurityException {
        mLocationClient.requestLocationUpdates(getLocationRequest(), this, null);
        mConsumer = consumer;
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void setFilter(@Nullable Filter<Location> filter) {
        mFilter = filter;
    }

    @Override
    public void stopUpdates(@NonNull LocationConsumer consumer) {
        mLocationClient.removeLocationUpdates(this);
        mConsumer = null;
    }

    private void handleLocationAvailabilityEvent(boolean isAvailable) {
        if (mConsumer != null) {
            mConsumer.onLocationUpdatesAvailabilityChange(isAvailable);
        }
    }

    @Override
    public void onLocationResult(LocationResult result) {
        for (Location location : result.getLocations()) {
            if (mFilter == null || mFilter.isRelevant(location)) {
                mConsumer.onNewLocationUpdate(location);
            }
        }
    }

    @Override
    public void onLocationAvailability(LocationAvailability availability) {
        handleLocationAvailabilityEvent(availability.isLocationAvailable());
    }
}
