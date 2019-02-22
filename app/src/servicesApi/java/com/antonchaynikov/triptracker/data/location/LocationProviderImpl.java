package com.antonchaynikov.triptracker.data.location;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class LocationProviderImpl extends LocationCallback implements TestableLocationProvider {

    private FusedLocationProviderClient mLocationClient;
    private LocationConsumer mConsumer;
    private Filter<Location> mFilter;

    LocationProviderImpl(@NonNull Context context) {
        mLocationClient = LocationServices.getFusedLocationProviderClient(context.getApplicationContext());
    }

    public void setTestMode() throws SecurityException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        mLocationClient.setMockMode(true).addOnCompleteListener(t -> {
            latch.countDown();
            Log.d("TAG", "testMode " + t.isSuccessful());
        });

        // return when setMockMode completes
        while(latch.getCount() > 0) {
            latch.await();
        }
    }

    @Override
    public void emitLocations(List<Location> locations) throws SecurityException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(locations.size());
        for (Location location: locations) {
            mLocationClient.setMockLocation(location).addOnCompleteListener(task -> {
                Log.d("TAG", "Setting location " + task.isSuccessful());
                latch.countDown();
            });
        }

        // return when all locations have been set
        while(latch.getCount() > 0) {
            latch.await();
        }
    }

    @Override
    public void setGeolocationAvailability(boolean isAvailable) {
        Log.d("TAG", "Setting geolocation");
        handleLocationAvailabilityEvent(isAvailable);
    }

    @Override
    public void startUpdates(@NonNull LocationConsumer consumer) throws SecurityException {
        Log.d("LocaProvider", "startUpdates");
        mLocationClient.requestLocationUpdates(getLocationRequest(), this, null );
        mConsumer = consumer;
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
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
        Log.d("TAG", "Location result is here");
        for (Location location: result.getLocations()) {
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
