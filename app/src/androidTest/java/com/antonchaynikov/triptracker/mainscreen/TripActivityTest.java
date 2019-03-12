package com.antonchaynikov.triptracker.mainscreen;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.location.LocationSource;
import com.antonchaynikov.triptracker.data.repository.Repository;
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
import java.util.concurrent.CountDownLatch;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class TripActivityTest {

    private static final int LOCATIONS_COUNT = 2;
    private static FirebaseAuth sFirebaseAuth;

    @Rule
    public final GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private ActivityTestRule<TripActivity> activityTestRule = new ActivityTestRule<>(TripActivity.class, true, false);

    private Repository mockRepository;
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
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mockRepository = new MockRepository();
        mockLocationSource = new MockLocationSource(createLocationsList());
        mViewModel = new TripViewModel(
                TripManager.getInstance(mockRepository, mockLocationSource, new StatisticsCalculator()),
                sFirebaseAuth,
                new StatisticsFormatter(context),
                true);

    }

    @After
    public void tearDown() throws Exception {
        TripManager.resetInstance();
    }

    @Test
    public void shouldShowStatistics_whenTripStarts() throws Exception {
        activityTestRule.launchActivity(TripActivity.getStartIntent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                sFirebaseAuth.getCurrentUser()
        ));
        TripActivity activity = activityTestRule.getActivity();
        activity.injectViewModel(mViewModel);
        // Expecting 2 updates
        IdlingResource idlingResource = activity.initStatisticsIdlingResource(LOCATIONS_COUNT + 1);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.btn_layout_statistics)).perform(click());

        onView(withId(R.id.tv_statistics_distance)).check(matches(withText("0,07 km")));
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    private List<Location> createLocationsList() {
        List<Location> locations = new ArrayList<>(LOCATIONS_COUNT);

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(12.3456);
        location.setLongitude(65.4321);
        location.setAccuracy(1);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        locations.add(location);

        location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(12.3452);
        location.setLongitude(65.4326);
        location.setAccuracy(1);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        locations.add(location);

        return locations;
    }
}