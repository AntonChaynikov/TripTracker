package com.antonchaynikov.triptracker.data.repository;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.CompletableSubject;

public final class FireStoreDB implements Repository {

    private static final String TRIPS_COLLECTION_NAME = "trips";

    private static volatile FireStoreDB sInstance;

    private FirebaseFirestore mDatabase;

    private FireStoreDB() {
        mDatabase = FirebaseFirestore.getInstance();
    }

    public static FireStoreDB getInstance() {
        if (sInstance == null) {
            synchronized (FireStoreDB.class) {
                if (sInstance == null) {
                    sInstance = new FireStoreDB();
                }
            }
        }
        return sInstance;
    }

    @Override
    public Completable startTrip(@NonNull Trip trip) {
        CompletableSubject completable = CompletableSubject.create();
        mDatabase.collection(TRIPS_COLLECTION_NAME)
                .document(Long.toString(trip.getStartDate()))
                .set(trip)
                .addOnCompleteListener(task -> completable.onComplete());
        return completable;
    }

    @Override
    public Observable<List<Trip>> getAllTrips() {
        return null;
    }

    @Override
    public Observable<Trip> getTripById(@NonNull String id) {
        return null;
    }

    @Override
    public Completable finishTrip(@NonNull Trip trip, long date) {
        CompletableSubject completable = CompletableSubject.create();
        trip.setEndDate(date);
        mDatabase.collection(TRIPS_COLLECTION_NAME)
                .document(Long.toString(trip.getStartDate()))
                .update(Trip.FIELD_NAME_END_DATE, date)
                .addOnCompleteListener(task -> completable.onComplete());
        return completable;
    }

    @Override
    public void addCoordinate(@NonNull TripCoordinate coordinate, @NonNull Trip trip) {
        mDatabase.collection(TRIPS_COLLECTION_NAME)
                .document(Long.toString(trip.getStartDate()))
                .collection(Trip.FIELD_NAME_COLLECTION_COORDINATES)
                .add(coordinate);
    }
}
