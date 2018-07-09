package com.antonchaynikov.triptracker;

import android.location.Location;

import io.reactivex.Observable;

public interface LocationSource {

    Observable<Location> getLocation();

    void toggleLocationUpdates();

    boolean isUpdateEnabled();

}
