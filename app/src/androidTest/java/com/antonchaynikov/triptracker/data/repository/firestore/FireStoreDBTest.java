package com.antonchaynikov.triptracker.data.repository.firestore;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.repository.Repository;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FireStoreDBTest {

    private static FirebaseAuth sFirebaseAuth;
    private Repository mTestSubject;

    @BeforeClass
    public static void initTestEnv() throws Exception {
        // Authenticate as a test user
        sFirebaseAuth = FirebaseAuth.getInstance();
        CountDownLatch authInProcessLatch = new CountDownLatch(1);
        sFirebaseAuth.signInWithEmailAndPassword("test@test.test", "123456").addOnCompleteListener(t -> authInProcessLatch.countDown());
        while(authInProcessLatch.getCount() > 0) {
            authInProcessLatch.await();
        }
        FireStoreDB.getInstance().deleteUserData().blockingAwait();
    }

    @Before
    public void setUp() throws Exception {
        mTestSubject = FireStoreDB.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        mTestSubject.deleteUserData().blockingAwait();
    }

    @Test
    public void addTrip_shouldAddTrip() throws Exception {
        long startDate = System.currentTimeMillis();
        Trip savedTrip = new Trip(startDate);
        savedTrip.updateStatistics(123, 321);

        mTestSubject.addTrip(savedTrip).blockingAwait();

        Trip trip = mTestSubject.getTripByStartDate(startDate).blockingFirst();
        assertEquals(savedTrip, trip);
    }

    @Test
    public void getAllTrips_shouldReturnAllTrips() throws Exception {
        int tripsCount = 5;
        for (int i = 0; i < tripsCount; i++) {
            mTestSubject.addTrip(new Trip(System.currentTimeMillis())).blockingAwait();
        }

        Set<Trip> tripsDistinct = new HashSet<>(mTestSubject.getAllTrips().blockingFirst());
        assertEquals(tripsCount, tripsDistinct.size());
    }

    @Test
    public void getAllTrips_shouldReturnEmptyList_ifNoTrips() throws Exception {
        Set<Trip> tripsDistinct = new HashSet<>(mTestSubject.getAllTrips().blockingFirst());
        assertTrue(tripsDistinct.isEmpty());
    }

    @Test
    public void getCoordinates_shouldReturnCoordinates() throws Exception {
        Trip trip = new Trip(System.currentTimeMillis());
        mTestSubject.addTrip(trip).blockingAwait();

        TripCoordinate tripCoordinate = new TripCoordinate();
        tripCoordinate.setDate(System.currentTimeMillis());
        tripCoordinate.setLatitude(123);
        tripCoordinate.setLongitude(321);
        mTestSubject.addCoordinate(tripCoordinate, trip).blockingAwait();

        TripCoordinate coord = mTestSubject.getCoordinatesForTrip(trip.getStartDate()).blockingFirst().get(0);

        assertEquals(tripCoordinate, coord);
    }

    @Test
    public void updateTrip_shouldUpdateTrip() throws Exception {
        long startDate = System.currentTimeMillis();
        Trip trip = new Trip(startDate);
        mTestSubject.addTrip(trip).blockingAwait();
        long endDate = System.currentTimeMillis() + 123456;
        trip.setEndDate(endDate);
        trip.updateStatistics(123,321);
        mTestSubject.updateTrip(trip);

        assertEquals(trip, mTestSubject.getTripByStartDate(startDate).blockingFirst());
    }

    @Test
    public void getTrip_shouldEmitError_ifTripMissing() {
        mTestSubject.getTripByStartDate(1234).blockingSubscribe(new Observer<Trip>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Trip trip) {
                throw new AssertionError("The error should have been emitted");
            }

            @Override
            public void onError(Throwable e) {
                assert(e instanceof NoSuchElementException);
            }

            @Override
            public void onComplete() {
                throw new AssertionError("The error should have been emitted");
            }
        });
    }
}