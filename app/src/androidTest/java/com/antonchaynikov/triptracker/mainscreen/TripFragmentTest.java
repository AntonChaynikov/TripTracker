package com.antonchaynikov.triptracker.mainscreen;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import com.antonchaynikov.triptracker.MockLocationSource;
import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.repository.Repository;
import com.antonchaynikov.triptracker.data.tripmanager.StatisticsCalculator;
import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class TripFragmentTest {

    private static final int LOCATIONS_COUNT = 2;
    private static FirebaseAuth sFirebaseAuth;

    @Rule
    public final GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    // Instantly returns Completable.complete()
    private Repository mockRepository;
    private TripViewModel mViewModel;
    private MockLocationSource mockLocationSource;

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
        FragmentScenario<TripFragment> scenario = FragmentScenario.launchInContainer(TripFragment.class);
        scenario.onFragment(fragment -> {
            IdlingResource idlingResource = fragment.initStatisticsIdlingResource(LOCATIONS_COUNT + 1);
            IdlingRegistry.getInstance().register(idlingResource);
            fragment.injectViewModel(mViewModel);
        });

        onView(withId(R.id.btn_layout_statistics)).perform(click());

        onView(withId(R.id.tv_statistics_distance)).check(matches(withText(new RegexMatcher("0(\\.|\\,)07 km"))));
        IdlingRegistry.getInstance().getResources().removeIf(resource -> resource.getName().equals("com.antonchaynikov.triptracker.mainscreen.TripFragment"));
    }

    @Test
    public void shouldShowGeolocationError_whenGeolocationIsUnavailable() throws Exception {
        FragmentScenario<TripFragment> scenario = FragmentScenario.launchInContainer(TripFragment.class, null, R.style.AppTheme, null);
        scenario.onFragment(fragment -> {
            fragment.injectViewModel(mViewModel);
        });

        onView(withId(R.id.btn_layout_statistics)).perform(click());

        mockLocationSource.onGeolocationAvailabilityChanged(false);

        onView(withText(R.string.message_geolocation_unavailable)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void shouldChangeActionButtonText_whenTripStarts() {
        FragmentScenario<TripFragment> scenario = FragmentScenario.launchInContainer(TripFragment.class);
        scenario.onFragment(fragment -> {
            fragment.injectViewModel(mViewModel);
        });

        ViewInteraction actionButtonInteraction = onView(withId(R.id.btn_layout_statistics));
        actionButtonInteraction.check(matches(withText(R.string.button_act)));
        actionButtonInteraction.perform(click());

        actionButtonInteraction.check(matches(withText(R.string.button_stop)));
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

    class RegexMatcher extends BaseMatcher<String> {

        private String mPattern;

        RegexMatcher(String pattern) {
            mPattern = pattern;
        }

        @Override
        public boolean matches(Object item) {
            if (item instanceof String) {
                return ((String) item).matches(mPattern);
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}