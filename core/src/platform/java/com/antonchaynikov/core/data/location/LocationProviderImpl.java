package com.antonchaynikov.core.data.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class LocationProviderImpl implements LocationProvider, LocationListener {

    private static final int PROVIDERS_COUNT = 2;

    private LocationManager mLocationManager;
    private LocationConsumer mConsumer;
    private Filter<Location> mFilter;
    private Set<String> mDisabledProviders;

    LocationProviderImpl(@NonNull Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void startUpdates(@NonNull LocationConsumer consumer) throws SecurityException {
        Log.d(LocationProviderImpl.class.getCanonicalName(), "platform LocationProviderImpl");
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
        mDisabledProviders = new HashSet<>(PROVIDERS_COUNT);
        mConsumer = consumer;
    }

    @Override
    public void setFilter(@Nullable Filter<Location> filter) {
        mFilter = filter;
    }

    @Override
    public void stopUpdates(@NonNull LocationConsumer consumer) {
        mLocationManager.removeUpdates(this);
        mConsumer = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mFilter == null || mFilter.isRelevant(location)) {
            mConsumer.onNewLocationUpdate(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        mDisabledProviders.remove(provider);
        if (mDisabledProviders.size() < PROVIDERS_COUNT) {
            mConsumer.onLocationUpdatesAvailabilityChange(true);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        mDisabledProviders.add(provider);
        if (mDisabledProviders.size() == PROVIDERS_COUNT) {
            mConsumer.onLocationUpdatesAvailabilityChange(false);
        }
    }
}

