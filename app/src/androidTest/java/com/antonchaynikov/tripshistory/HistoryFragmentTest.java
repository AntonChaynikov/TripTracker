package com.antonchaynikov.tripshistory;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.platform.app.InstrumentationRegistry;

import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.repository.firestore.FireStoreDB;
import com.antonchaynikov.core.viewmodel.StatisticsFormatter;
import com.antonchaynikov.triptracker.AndroidTestUtils;
import com.antonchaynikov.triptracker.R;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class HistoryFragmentTest {

    private static FirebaseAuth sFirebaseAuth;

    private FireStoreDB mFirestore;
    private Trip mTrip;

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
        mTrip = new Trip(System.currentTimeMillis());
        mTrip.updateStatistics(1000, 36);
        mFirestore.addTrip(mTrip);
    }

    @Test
    public void shouldShowTripsStatistics_whenFragmentIsVisible() throws Exception {
        Bundle args = new Bundle();
        args.putLong("tripStartDate", mTrip.getStartDate());

        FragmentScenario<HistoryFragment> scenario = FragmentScenario.launchInContainer(HistoryFragment.class, args);
        scenario.onFragment(fragment -> IdlingRegistry.getInstance().register(fragment.getIdlingResource()));

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String expectedStartDate = new StatisticsFormatter(context).formatTrip(mTrip).getStartDate();
        onView(withId(R.id.tv_statistics_extended_start_date)).check(matches(withText(expectedStartDate)));

        AndroidTestUtils.unregisterIdlingResource("HistoryFragment");
    }

    @After
    public void tearDown() throws Exception {
        mFirestore.deleteUserData().blockingAwait();
    }
}