package com.antonchaynikov.triptracker.MapActivity;

import android.location.Location;

import io.reactivex.Observable;

public interface LocationSource {

    void toggleLocationUpdates();

    boolean isUpdateEnabled();

    Observable<Location> getLocationUpdates();
}
