package com.antonchaynikov.triptracker.data;

import android.location.Location;

import androidx.annotation.NonNull;
import io.reactivex.Observable;

public interface LocationSource {

    void startUpdates();

    void stopUpdates();

    boolean isUpdateAvailable();

    boolean isLocationsUpdateEnabled();

    @NonNull
    Observable<Location> getLocationUpdates();
}
