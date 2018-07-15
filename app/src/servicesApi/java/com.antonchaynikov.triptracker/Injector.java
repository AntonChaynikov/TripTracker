package com.antonchaynikov.triptracker;


import android.content.Context;

public class Injector {
    public static LocationSource injectLocationSource(Context context, LocationUpdatePolicy updatePolicy) {
        return ServiceLocationSource.getInstance(context, updatePolicy);
    }
}
