package com.antonchaynikov.triptracker.data.location;

import android.location.Location;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

class MockLocationProvider implements LocationProvider {

    private Observable<Location> mLocationObservable;
    private Disposable mDisposable;
    private volatile LocationConsumer mConsumer;

    MockLocationProvider(Observable<Location> locations) {
        mLocationObservable = locations;
    }

    void onGeolocationAvailabilityChanged(boolean isAvailable) {
        if (mConsumer != null) {
            mConsumer.onLocationUpdatesAvailabilityChange(isAvailable);
        }
    }

    @Override
    public void startUpdates(@NonNull LocationConsumer consumer) {
        mConsumer = consumer;
        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        mDisposable = mLocationObservable.subscribe(consumer::onNewLocationUpdate);
    }

    @Override
    public void stopUpdates(@NonNull LocationConsumer consumer) {
        mDisposable.dispose();
    }

    @Override
    public void setFilter(@NonNull Filter<Location> filter) {

    }
}
