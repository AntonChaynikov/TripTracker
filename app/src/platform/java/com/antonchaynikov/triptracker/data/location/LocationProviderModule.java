package com.antonchaynikov.triptracker.data.location;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class LocationProviderModule {

    private LocationProviderModule() {}

    public static LocationProvider provide(@NonNull Context context, @Nullable Filter<Location> locationFilter) {
        LocationProvider locationProvider = new LocationProviderImpl(context);
        locationProvider.setFilter(locationFilter);
        return locationProvider;
    }
}
