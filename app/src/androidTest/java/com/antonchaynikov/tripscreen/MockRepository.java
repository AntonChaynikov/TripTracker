package com.antonchaynikov.tripscreen;

import androidx.annotation.NonNull;

import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.model.TripCoordinate;
import com.antonchaynikov.core.data.repository.Repository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

class MockRepository implements Repository {

    @Override
    public Completable addTrip(@NonNull Trip trip) {
        return Completable.complete();
    }

    @Override
    public Observable<List<Trip>> getAllTrips() {
        return Observable.empty();
    }

    @Override
    public Observable<Trip> getTripByStartDate(long startDate) {
        return Observable.empty();
    }

    @Override
    public Observable<List<TripCoordinate>> getCoordinatesForTrip(long tripStartDate) {
        return Observable.empty();
    }

    @Override
    public Completable updateTrip(@NonNull Trip trip) {
        return Completable.complete();
    }

    @Override
    public Completable addCoordinate(@NonNull TripCoordinate coordinate, @NonNull Trip trip) {
        return Completable.complete();
    }

    @Override
    public Completable deleteUserData() {
        return Completable.complete();
    }
}
