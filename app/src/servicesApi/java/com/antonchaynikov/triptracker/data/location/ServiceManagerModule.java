package com.antonchaynikov.triptracker.data.location;

import android.content.Context;

import androidx.annotation.NonNull;

public final class ServiceManagerModule {

    private ServiceManagerModule() {}

    @SuppressWarnings("unchecked")
    public static ServiceManager<?> provide(@NonNull Context context) {
        return ServiceManager.getInstance(context, FusedLocationService.class);
    }
}
