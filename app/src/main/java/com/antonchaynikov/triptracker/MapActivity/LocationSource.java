package com.antonchaynikov.triptracker.MapActivity;

import android.location.Location;

import io.reactivex.Observable;

public interface LocationSource {

    void startUpdates();

    void stopUpdates();

    boolean isUpdateEnabled();

    Observable<Location> getLocationUpdates();
}
