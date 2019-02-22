package com.antonchaynikov.triptracker.data.repository.firestore;

import android.util.Log;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.repository.Repository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.PublishSubject;

public final class FireStoreDB implements Repository {

    private final static String TAG = FireStoreDB.class.getCanonicalName();

    private static final String TRIPS_COLLECTION_NAME = "trips";
    private static final String ROOT_USER_COLLECTION = "users";
    private static final String COORDINATES_COLLECTION_NAME = "coordinates";

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
    public Completable addTrip(@NonNull Trip trip) {
        CompletableSubject completable = CompletableSubject.create();
        CollectionReference tripsCollectionRef = null;
        try {
            tripsCollectionRef = getTripsCollectionReference();
        } catch (FirebaseAuthException e) {
            completable.onError(e);
            e.printStackTrace();
        }
        if (tripsCollectionRef != null) {
            tripsCollectionRef
                    .document(Long.toString(trip.getStartDate()))
                    .set(trip)
                    .addOnCompleteListener(task -> completable.onComplete())
                    .addOnFailureListener(task -> Log.d(TAG, "Error adding/updating trip"));
        }
        return completable;
    }

    @Override
    public Observable<List<Trip>> getAllTrips() {
        PublishSubject<List<Trip>> observable = PublishSubject.create();
        CollectionReference tripsCollectionRef = null;
        try {
            tripsCollectionRef = getTripsCollectionReference();
        } catch (FirebaseAuthException e) {
            observable.onError(e);
            e.printStackTrace();
        }
        if (tripsCollectionRef != null) {
            tripsCollectionRef
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            observable.onNext(task.getResult().toObjects(Trip.class));
                        }
                    });
        }
        return observable;
    }

    @Override
    public Observable<Trip> getTripByStartDate(long startDate) {
        PublishSubject<Trip> observable = PublishSubject.create();
        CollectionReference tripsCollectionRef = null;
        try {
            tripsCollectionRef = getTripsCollectionReference();
        } catch (FirebaseAuthException e) {
            observable.onError(e);
            e.printStackTrace();
        }
        if (tripsCollectionRef != null) {
            tripsCollectionRef
                    .document(Long.toString(startDate))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            observable.onNext(task.getResult().toObject(Trip.class));
                        }
                    });
        }
        return observable;
    }

    @Override
    public Observable<List<TripCoordinate>> getCoordinatesForTrip(long tripsStartDate) {
        PublishSubject<List<TripCoordinate>> observable = PublishSubject.create();
        CollectionReference tripsCollectionRef = null;
        try {
            tripsCollectionRef = getTripsCollectionReference();
        } catch (FirebaseAuthException e) {
            observable.onError(e);
            e.printStackTrace();
        }
        if (tripsCollectionRef != null) {
            tripsCollectionRef
                    .document(Long.toString(tripsStartDate))
                    .collection(COORDINATES_COLLECTION_NAME)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            observable.onNext(task.getResult().toObjects(TripCoordinate.class));
                        }
                    });
        }
        return observable;
    }

    @Override
    public Completable updateTrip(@NonNull Trip trip) {
        return addTrip(trip);
    }

    @Override
    public Completable addCoordinate(@NonNull TripCoordinate coordinate, @NonNull Trip trip) {
        CompletableSubject completable = CompletableSubject.create();
        CollectionReference tripsCollectionRef = null;
        try {
            tripsCollectionRef = getTripsCollectionReference();
        } catch (FirebaseAuthException e) {
            completable.onError(e);
            e.printStackTrace();
        }
        if (tripsCollectionRef != null) {
            tripsCollectionRef
                    .document(Long.toString(trip.getStartDate()))
                    .collection(COORDINATES_COLLECTION_NAME)
                    .add(coordinate);
        }
        return completable;
    }

    private CollectionReference getTripsCollectionReference() throws FirebaseAuthException {
        String uId = FirebaseAuth.getInstance().getUid();
        if (uId == null) {
            throw new FirebaseAuthException("", "Couldn't find user id");
        }
        return mDatabase
                .collection(ROOT_USER_COLLECTION)
                .document(uId)
                .collection(TRIPS_COLLECTION_NAME);
    }
}
