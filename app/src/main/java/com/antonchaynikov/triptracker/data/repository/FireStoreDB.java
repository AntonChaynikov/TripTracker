package com.antonchaynikov.triptracker.data.repository;

import android.util.Log;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.LocalDateTime;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class FireStoreDB implements Repository {

    private final static String TRIPS_COLLECTION_NAME = "trips";

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
        return Completable.complete();
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
        return Completable.complete();
    }

    @Override
    public void addCoordinate(@NonNull TripCoordinate coordinate, @NonNull Trip trip) {

    }
}
