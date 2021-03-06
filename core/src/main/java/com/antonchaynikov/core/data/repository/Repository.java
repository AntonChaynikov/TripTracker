package com.antonchaynikov.core.data.repository;

import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.model.TripCoordinate;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;

public interface Repository {

    Completable addTrip(@NonNull Trip trip);

    Observable<List<Trip>> getAllTrips();

    Observable<Trip> getTripByStartDate(long startDate);

    Observable<List<TripCoordinate>> getCoordinatesForTrip(long tripStartDate);

    Completable updateTrip(@NonNull Trip trip);

    Completable addCoordinate(@NonNull TripCoordinate coordinate, @NonNull Trip trip);

    Completable deleteUserData();
}
