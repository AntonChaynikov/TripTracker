package com.antonchaynikov.triptracker;


import android.content.Context;

import com.antonchaynikov.triptracker.MapActivity.LocationSource;
import com.antonchaynikov.triptracker.MapActivity.LocationUpdatePolicy;

public class Injector {
    public static LocationSource injectLocationSource(Context context, LocationUpdatePolicy updatePolicy) {
        return ServiceLocationSource.getInstance(context, updatePolicy);
    }
}
