package com.antonchaynikov.triptracker.data;

import android.location.Location;

import androidx.annotation.NonNull;
import io.reactivex.Observable;

public interface LocationSource {

    void startUpdates();

    void stopUpdates();

    boolean isUpdateEnabled();

    @NonNull
    Observable<Location> getLocationUpdates();
}
