package com.antonchaynikov.triptracker.application;

import com.antonchaynikov.triptracker.injection.Injector;
import com.crashlytics.android.Crashlytics;

import androidx.multidex.MultiDexApplication;
import io.fabric.sdk.android.Fabric;

public class TripApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Injector.init(getApplicationContext());
    }
}
