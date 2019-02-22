package com.antonchaynikov.triptracker.data.repository.firestore;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.repository.Repository;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class FireStoreDBTest {

    private FirebaseAuth mFirebaseAuth;
    private Repository mTestSubject;

    @BeforeClass
    public void init() throws Exception {
        // Authenticate as a test user
        mFirebaseAuth = FirebaseAuth.getInstance();
        CountDownLatch authInProcessLatch = new CountDownLatch(1);
        mFirebaseAuth.signInWithEmailAndPassword("test@test.test", "123456").addOnCompleteListener(t -> authInProcessLatch.countDown());
        while(authInProcessLatch.getCount() > 0) {
            authInProcessLatch.await();
        }
    }

    @Before
    public void setUp() throws Exception {
        mTestSubject = FireStoreDB.getInstance();
    }

    @After
    public void tearDown() {
        mTestSubject.deleteUserData();
    }

    @Test
    public void addTrip_shouldAddTrip() {
        Trip trip = new Trip(System.currentTimeMillis());
        mTestSubject.addTrip(trip).subscribe()
    }

}