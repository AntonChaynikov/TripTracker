package com.antonchaynikov.triptracker.data;

import android.content.ServiceConnection;
import android.location.LocationManager;

import androidx.annotation.NonNull;

public class LocationSourceInjector {

    public static LocationSource get() {
        return getPlatformLocationSource();
    }

    public static ServiceConnection getServiceConnection() {
        return getPlatformLocationSource();
    }

    private static PlatformLocationSource getPlatformLocationSource() {
        return PlatformLocationSource.getLocationSource();
    }


}
