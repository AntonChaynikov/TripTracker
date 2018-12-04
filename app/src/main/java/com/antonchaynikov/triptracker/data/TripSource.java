package com.antonchaynikov.triptracker.data;

import android.location.Location;

import androidx.annotation.NonNull;
import io.reactivex.Observable;

public interface TripSource {

    void startTrip();

    Trip finishTrip();

    boolean isLocationsUpdateAvailable();

    boolean isLocationsUpdateEnabled();

    @NonNull
    Observable<Location> getLocationUpdates();
}
