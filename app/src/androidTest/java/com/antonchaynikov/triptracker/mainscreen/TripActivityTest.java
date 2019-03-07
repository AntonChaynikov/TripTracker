package com.antonchaynikov.triptracker.mainscreen;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.repository.firestore.FireStoreDB;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class TripActivityTest {

    private static FirebaseAuth sFirebaseAuth;
    private ActivityTestRule<TripActivity> activityTestRule = new ActivityTestRule<>(TripActivity.class, true, false);

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
    }

    @Test
    public void shouldShowStatistics_whenTripStarts() throws Exception {
        activityTestRule.launchActivity(TripActivity.getStartIntent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                sFirebaseAuth.getCurrentUser()
        ));

        onView(withId(R.id.btn_layout_statistics)).perform(click());


    }

    @After
    public void tearDown() throws Exception {
        mFirestore.deleteUserData();
    }
}