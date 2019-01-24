package com.antonchaynikov.triptracker.data.location;

import android.content.Context;

import androidx.annotation.NonNull;

public class PlatformServiceManagerInjector {
    @SuppressWarnings("unchecked")
    ServiceManager<PlatformLocationService> inject(@NonNull Context context, @NonNull LocationSource locationSource) {
        return ServiceManager.getInstance(context, locationSource, PlatformLocationService.class);
    }
}
