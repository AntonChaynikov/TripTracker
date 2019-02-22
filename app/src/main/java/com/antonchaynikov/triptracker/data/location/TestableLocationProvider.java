package com.antonchaynikov.triptracker.data.location;

import android.location.Location;

import java.util.List;

import io.reactivex.Completable;

interface TestableLocationProvider extends LocationProvider {
    void setTestMode()  throws SecurityException, InterruptedException;
    void emitLocations(List<Location> locations) throws SecurityException, InterruptedException;
    void setGeolocationAvailability(boolean isAvailable);
}
