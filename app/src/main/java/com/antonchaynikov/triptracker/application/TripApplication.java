package com.antonchaynikov.triptracker.application;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDexApplication;

import com.antonchaynikov.core.RepositoryModule;
import com.antonchaynikov.core.data.location.LocationSourceModule;
import com.antonchaynikov.core.injection.Injector;
import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.fabric.sdk.android.Fabric;

public class TripApplication extends MultiDexApplication implements HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        /*
        DaggerAppComponent.builder()
                .context(getApplicationContext())
                .build()
                .inject(this);
                */
        Injector.init(DaggerAndroidInjector.INSTANCE);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
