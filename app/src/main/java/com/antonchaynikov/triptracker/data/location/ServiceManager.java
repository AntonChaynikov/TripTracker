package com.antonchaynikov.triptracker.data.location;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public final class ServiceManager<T extends LocationService> {

    @SuppressLint("StaticFieldLeak")
    private static volatile ServiceManager<? extends LocationService> sInstance;

    private Context mAppContext;
    private LocationSource mLocationSource;
    private Class<T> mServiceClass;

    private ServiceManager(@NonNull Context context, @NonNull LocationSource locationSource, @NonNull Class<T> serviceClass ) {
        mAppContext = context.getApplicationContext();
        mLocationSource = locationSource;
        mServiceClass = serviceClass;
    }

    public static <K extends LocationService> ServiceManager getInstance(
            @NonNull Context context,
            @NonNull LocationSource locationSource,
            @NonNull Class<K> serviceClass) {

        if (sInstance == null) {
            synchronized (ServiceManager.class) {
                if (sInstance == null) {
                    sInstance = new ServiceManager<>(context, locationSource, serviceClass);
                }
            }
        }
        return sInstance;
    }

    public  void startLocationService() {
        Intent service = new Intent(mAppContext, mServiceClass);
        if (!isServiceRunning()) {
            ActivityCompat.startForegroundService(mAppContext, service);
        }
        mAppContext.bindService(service, mLocationSource, Context.BIND_AUTO_CREATE);
    }

    public void stopLocationService() {
        mAppContext.unbindService(mLocationSource);
        if (isServiceRunning()) {
            mAppContext.stopService(new Intent(mAppContext,  mServiceClass));
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) mAppContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (mServiceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
