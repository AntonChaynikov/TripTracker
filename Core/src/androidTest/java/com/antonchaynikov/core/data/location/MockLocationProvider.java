package com.antonchaynikov.core.data.location;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

@VisibleForTesting
public class MockLocationProvider implements LocationProvider {

    private Observable<Location> mLocationObservable;
    private Disposable mDisposable;
    private volatile LocationConsumer mConsumer;

    @VisibleForTesting
    public MockLocationProvider(Observable<Location> locations) {
        mLocationObservable = locations;
    }

    @VisibleForTesting
    public void onGeolocationAvailabilityChanged(boolean isAvailable) {
        if (mConsumer != null) {
            mConsumer.onLocationUpdatesAvailabilityChange(isAvailable);
        }
    }

    @Override
    public void startUpdates(@NonNull LocationConsumer consumer) {
        Log.d("MockLocaProvider", "startUpdates");
        mConsumer = consumer;
        mDisposable = mLocationObservable.subscribe(consumer::onNewLocationUpdate);
    }

    @Override
    public void stopUpdates(@NonNull LocationConsumer consumer) {
        mDisposable.dispose();
    }

    @Override
    public void setFilter(@Nullable Filter<Location> filter) {

    }
}
