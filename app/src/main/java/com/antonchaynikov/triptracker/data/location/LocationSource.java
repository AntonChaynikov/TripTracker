package com.antonchaynikov.triptracker.data.location;

import android.location.Location;

import io.reactivex.Observable;

public interface LocationSource {
    void startUpdates();

    void finishUpdates();

    Observable<Location> getLocationsObservable();

    Observable<Boolean> getGeolocationAvailabilityObservable();
}
