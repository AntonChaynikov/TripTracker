package com.antonchaynikov.triptracker.application;

import android.app.Activity;

import androidx.multidex.MultiDexApplication;

import com.antonchaynikov.core.RepositoryModule;
import com.antonchaynikov.core.data.location.LocationSourceModule;
import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.fabric.sdk.android.Fabric;

public class TripApplication extends MultiDexApplication implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        DaggerAppComponent.builder()
                .context(getApplicationContext())
                .build()
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }
}
