package com.antonchaynikov.triptracker.data.repository;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;

public interface Repository {

    Completable addTrip(@NonNull Trip trip);

    Observable<List<Trip>> getAllTrips();

    Observable<Trip> getTripById(@NonNull String id);

    Completable updateTrip(@NonNull Trip trip);

    void addCoordinate(@NonNull TripCoordinate coordinate, @NonNull Trip trip);

}
