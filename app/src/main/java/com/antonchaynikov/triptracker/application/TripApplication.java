package com.antonchaynikov.triptracker.application;

import androidx.multidex.MultiDexApplication;

import com.antonchaynikov.core.injection.Injector;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class TripApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        MainInjector.INSTANCE.init(this);
        Injector.init(MainInjector.INSTANCE);
    }
}
