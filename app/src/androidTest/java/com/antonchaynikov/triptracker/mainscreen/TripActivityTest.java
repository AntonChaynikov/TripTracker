package com.antonchaynikov.triptracker.mainscreen;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.location.LocationSource;
import com.antonchaynikov.triptracker.data.repository.firestore.FireStoreDB;
import com.antonchaynikov.triptracker.data.tripmanager.StatisticsCalculator;
import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class TripActivityTest {

    private static final int LOCATIONS_COUNT = 2;
    private static FirebaseAuth sFirebaseAuth;

    @Rule
    public final GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private ActivityTestRule<TripActivity> activityTestRule = new ActivityTestRule<>(TripActivity.class, true, false);

    private FireStoreDB mFirestore;
    private TripViewModel mViewModel;
    private LocationSource mockLocationSource;

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
        MockitoAnnotations.initMocks(this);
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mFirestore = FireStoreDB.getInstance();
        mockLocationSource = new MockLocationSource(createLocationsList());
        mViewModel = new TripViewModel(
                TripManager.getInstance(FireStoreDB.getInstance(), mockLocationSource, new StatisticsCalculator()),
                sFirebaseAuth,
                new StatisticsFormatter(context),
                true);

    }

    @Test
    public void shouldShowStatistics_whenTripStarts() throws Exception {
        activityTestRule.launchActivity(TripActivity.getStartIntent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                sFirebaseAuth.getCurrentUser()
        ));
        activityTestRule.getActivity().injectViewModel(mViewModel);

        onView(withId(R.id.btn_layout_statistics)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        mFirestore.deleteUserData();
    }

    private List<Location> createLocationsList() {
        List<Location> locations = new ArrayList<>(LOCATIONS_COUNT);
        Random r = new Random();
        for (int i = 0; i < LOCATIONS_COUNT; i++) {
            Location location = new Location(LocationManager.GPS_PROVIDER);
            double diff = r.nextInt(9) * 0.00001;
            location.setLatitude(12.3456 + diff);
            location.setLongitude(65.4321 + diff);
            location.setAccuracy(1);
            location.setTime(System.currentTimeMillis());
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            locations.add(location);
        }
        return locations;
    }
}