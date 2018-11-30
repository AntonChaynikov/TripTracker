package com.antonchaynikov.triptracker.data;

import android.location.LocationManager;

import androidx.annotation.NonNull;

public class LocationSourceInjector {
    public static LocationSource get(@NonNull LocationManager locationManager) {
        return PlatformLocationSource.getLocationSource(locationManager, new LocationFilter());
    }
}
