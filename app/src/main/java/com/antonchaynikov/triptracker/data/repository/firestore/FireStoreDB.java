package com.antonchaynikov.triptracker.data.repository.firestore;

import android.os.OperationCanceledException;
import android.util.Log;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.repository.Repository;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

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
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            completable.onComplete();
                        } else {
                            if (task.isCanceled()) {
                                completable.onError(new OperationCanceledException());
                            } else {
                                completable.onError(task.getException());
                            }
                        }
                    });
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
                        } else {
                            if (task.isCanceled()) {
                                observable.onError(new OperationCanceledException());
                            } else {
                                observable.onError(task.getException());
                            }
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
                            if (task.getResult().exists()) {
                                observable.onNext(task.getResult().toObject(Trip.class));
                            } else {
                                observable.onError(new NoSuchElementException("Failed to fetch an element/element doesn't exist"));
                            }

                        } else {
                            if (task.isCanceled()) {
                                observable.onError(new OperationCanceledException());
                            } else {
                                observable.onError(task.getException());
                            }
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
                        } else {
                            if (task.isCanceled()) {
                                observable.onError(new OperationCanceledException());
                            } else {
                                observable.onError(task.getException());
                            }
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
                    .add(coordinate)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            completable.onComplete();
                        } else {
                            if (task.isCanceled()) {
                                completable.onError(new OperationCanceledException());
                            } else {
                                completable.onError(task.getException());
                            }
                        }
                    });
        }
        return completable;
    }

    @Override
    public Completable deleteUserData() {
        CompletableSubject completable = CompletableSubject.create();
        CollectionReference tripsCollectionRef = null;
        try {
            tripsCollectionRef = getTripsCollectionReference();
        } catch (FirebaseAuthException e) {
            completable.onError(e);
            e.printStackTrace();
        }
        if (tripsCollectionRef != null) {
            tripsCollectionRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot documentSnapshot: documents) {
                        documentSnapshot.getReference().delete();
                    }
                    completable.onComplete();
                } else {
                    if (task.isCanceled()) {
                        completable.onError(new OperationCanceledException());
                    } else {
                        task.getException().printStackTrace();
                        completable.onError(task.getException());
                    }
                }
            });
        }
        return completable;
    }

    private CollectionReference getTripsCollectionReference() throws FirebaseAuthException {
        return getUserDocumentReference()
                .collection(TRIPS_COLLECTION_NAME);
    }

    private DocumentReference getUserDocumentReference() throws FirebaseAuthException {
        String uId = FirebaseAuth.getInstance().getUid();
        if (uId == null) {
            throw new FirebaseAuthException("", "Couldn't find user id");
        }
        return mDatabase
                .collection(ROOT_USER_COLLECTION)
                .document(uId);
    }
}
