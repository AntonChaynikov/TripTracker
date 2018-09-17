package com.antonchaynikov.triptracker;

import android.content.Context;

import com.antonchaynikov.triptracker.MapActivity.LocationSource;

public class Injector {

    public static LocationSource injectLocationSource(Context context) {
        return PlatformLocationSource.getInstance(context);
    }

}
