package com.antonchaynikov.triptracker.data;

import android.content.ServiceConnection;
import android.location.Location;

import androidx.annotation.NonNull;

public class LocationSourceInjector {

    public static TripSource get(@NonNull Filter<Location> location) {
        return getPlatformLocationSource(location);
    }

    public static ServiceConnection getServiceConnection(@NonNull Filter<Location> location) {
        return getPlatformLocationSource(location);
    }

    private static PlatformLocationSource getPlatformLocationSource(@NonNull Filter<Location> location) {
        return PlatformLocationSource.getLocationSource(location);
    }

}
