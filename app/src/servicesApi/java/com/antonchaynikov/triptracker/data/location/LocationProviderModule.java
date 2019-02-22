package com.antonchaynikov.triptracker.data.location;

import android.content.Context;

import androidx.annotation.NonNull;

public class LocationProviderModule {
    public static LocationProvider provide(@NonNull Context context) {
        return new LocationProviderImpl(context);
    }
}
