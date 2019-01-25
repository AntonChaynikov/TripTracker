package com.antonchaynikov.triptracker.data.location;

import android.content.Context;

import androidx.annotation.NonNull;

public class PlatformServiceManagerInjector {
    @SuppressWarnings("unchecked")
    ServiceManager<PlatformLocationService> inject(@NonNull Context context) {
        return ServiceManager.getInstance(context, PlatformLocationService.class);
    }
}
