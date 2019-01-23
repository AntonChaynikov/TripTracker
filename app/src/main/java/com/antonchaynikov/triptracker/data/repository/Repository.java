package com.antonchaynikov.triptracker.data.repository;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;

public interface Repository {

    Completable startTrip(@NonNull Trip trip);

    Observable<List<Trip>> getAllTrips();

    Observable<Trip> getTripById(@NonNull String id);

    Completable finishTrip(@NonNull Trip trip, long date);

    void addCoordinate(@NonNull TripCoordinate coordinate, @NonNull Trip trip);

}
