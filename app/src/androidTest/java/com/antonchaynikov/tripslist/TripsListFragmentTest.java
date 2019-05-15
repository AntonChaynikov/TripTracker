package com.antonchaynikov.tripslist;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.IdlingRegistry;

import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.repository.firestore.FireStoreDB;
import com.antonchaynikov.triptracker.AndroidTestUtils;
import com.antonchaynikov.triptracker.R;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class TripsListFragmentTest {

    private static final int DB_TRIPS_COUNT = 2;
    private static FirebaseAuth sFirebaseAuth;

    private FireStoreDB mFirestore;

    @BeforeClass
    public static void initTestEnv() throws Exception {
        // Authenticate as a test user
        sFirebaseAuth = FirebaseAuth.getInstance();
        CountDownLatch authInProcessLatch = new CountDownLatch(1);
        sFirebaseAuth.signInWithEmailAndPassword("test@test.test", "123456").addOnCompleteListener(t -> authInProcessLatch.countDown());
        while (authInProcessLatch.getCount() > 0) {
            authInProcessLatch.await();
        }
        FireStoreDB.getInstance().deleteUserData().blockingAwait();
    }

    @Before
    public void setUp() throws Exception {
        mFirestore = FireStoreDB.getInstance();
        for (int i = 0; i < DB_TRIPS_COUNT; i++) {
            Trip trip = new Trip(System.currentTimeMillis());
            trip.updateStatistics(i, i);
            trip.setEndDate(System.currentTimeMillis());
            mFirestore.addTrip(trip).blockingAwait();
        }
    }

    @Test
    public void shouldShowTrips_whenActivityResumes() throws Exception {
        FragmentScenario<TripsListFragment> scenario = FragmentScenario.launchInContainer(TripsListFragment.class);
        scenario.onFragment(fragment -> {
            IdlingRegistry.getInstance().register(fragment.getIdlingResource());
        });

        onView(withId(R.id.rv_trips_list)).check(new RecyclerViewItemCountAssertion(DB_TRIPS_COUNT));
        AndroidTestUtils.unregisterIdlingResource("TripsListFragment");
    }

    @After
    public void tearDown() throws Exception {
        mFirestore.deleteUserData().blockingAwait();
    }
}