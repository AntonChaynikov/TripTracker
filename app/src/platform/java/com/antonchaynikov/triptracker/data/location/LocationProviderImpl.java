package com.antonchaynikov.triptracker.data.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;

class LocationProviderImpl implements TestableLocationProvider, LocationListener {

    private LocationManager mLocationManager;
    private LocationConsumer mConsumer;
    private Filter<Location> mFilter;
    private Set<String> mDisabledProviders;
    private int mProvidersCount;

    LocationProviderImpl(@NonNull Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void setTestMode() throws SecurityException {
        mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        mLocationManager.setTestProviderEnabled(LocationManager.NETWORK_PROVIDER, true);
    }

    @Override
    public void emitLocations(List<Location> locations) {
        for (Location location: locations) {
            location.setProvider(LocationManager.NETWORK_PROVIDER);
            mLocationManager.setTestProviderLocation(LocationManager.NETWORK_PROVIDER, location);
        }
    }

    @Override
    public void setGeolocationAvailability(boolean isAvailable) {
        if (isAvailable) {
            onProviderEnabled(LocationManager.NETWORK_PROVIDER);
            onProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            onProviderDisabled(LocationManager.NETWORK_PROVIDER);
            onProviderDisabled(LocationManager.GPS_PROVIDER);
        }
    }

    @Override
    public void startUpdates(@NonNull LocationConsumer consumer) throws SecurityException {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mProvidersCount = 2;
        mDisabledProviders = new HashSet<>(mProvidersCount);
        mConsumer = consumer;
    }

    @Override
    public void setFilter(@NonNull Filter<Location> filter) {
        mFilter = filter;
    }

    @Override
    public void stopUpdates(@NonNull LocationConsumer consumer) {
        mLocationManager.removeUpdates(this);
        mConsumer = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mFilter.isRelevant(location)) {
            mConsumer.onNewLocationUpdate(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        mDisabledProviders.remove(provider);
        if (mDisabledProviders.size() < mProvidersCount) {
            mConsumer.onLocationUpdatesAvailabilityChange(true);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        mDisabledProviders.add(provider);
        if (mDisabledProviders.size() == mProvidersCount) {
            mConsumer.onLocationUpdatesAvailabilityChange(false);
        }
    }
}

