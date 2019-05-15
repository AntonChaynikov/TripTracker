package com.antonchaynikov.triptracker.mainscreen;

import android.Manifest;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.GrantPermissionRule;

import com.antonchaynikov.triptracker.AndroidTestUtils;
import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
    public final TripFragmentMockInjectionRule tripFragmentMockInjectionRule = new TripFragmentMockInjectionRule(createLocationsList());

    @Rule
    public final GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

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

    @After
    public void tearDown() throws Exception {
        TripManager.resetInstance();
    }

    @Test
    public void shouldShowStatistics_whenTripStarts() throws Exception {
        FragmentScenario.launchInContainer(TripFragment.class)
                .onFragment(fragment -> {
                    IdlingResource idlingResource = fragment.initStatisticsIdlingResource(LOCATIONS_COUNT);
                    IdlingRegistry.getInstance().register(idlingResource);
                });

        onView(withId(R.id.btn_layout_statistics)).perform(click());

        onView(withId(R.id.tv_statistics_distance)).check(matches(withText(new RegexMatcher("0(\\.|\\,)07 km"))));
        AndroidTestUtils.unregisterIdlingResource("com.antonchaynikov.triptracker.mainscreen.TripFragment");
    }

    @Test
    public void shouldShowGeolocationError_whenGeolocationIsUnavailable() throws Exception {
        FragmentScenario.launchInContainer(TripFragment.class, null, R.style.AppTheme, null);

        onView(withId(R.id.btn_layout_statistics)).perform(click());

        tripFragmentMockInjectionRule.getInjectedLocationSource().onGeolocationAvailabilityChanged(false);

        onView(withText(R.string.message_geolocation_unavailable)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void shouldChangeActionButtonText_whenTripStarts() {
        FragmentScenario.launchInContainer(TripFragment.class);

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