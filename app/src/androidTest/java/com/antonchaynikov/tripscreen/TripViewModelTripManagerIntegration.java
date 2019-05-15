package com.antonchaynikov.tripscreen;

import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.model.TripCoordinate;
import com.antonchaynikov.core.data.repository.Repository;
import com.antonchaynikov.core.viewmodel.TripStatistics;
import com.antonchaynikov.triptracker.R;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.observers.BaseTestConsumer;
import io.reactivex.observers.TestObserver;
import it.cosenonjaviste.daggermock.InjectFromComponent;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class TripViewModelTripManagerIntegration {

    @ClassRule
    public static final RxImmediateSchedulerRule IMMEDIATE_SCHEDULER_RULE = new RxImmediateSchedulerRule();

    private static final int LOCATIONS_COUNT = 2;

    @Rule
    public final TripViewModelInjectionRule viewModelInjectionRule = new TripViewModelInjectionRule(createLocationsList(LOCATIONS_COUNT));

    @InjectFromComponent(TripFragment.class)
    private TripViewModel mViewModel;

    @Mock
    private Repository mockRepository;

    @BeforeClass
    public static void setTestEnv() throws Exception {
        // Authenticate as a test user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        CountDownLatch authInProcessLatch = new CountDownLatch(1);
        firebaseAuth.signInWithEmailAndPassword("test@test.test", "123456").addOnCompleteListener(t -> authInProcessLatch.countDown());
        while (authInProcessLatch.getCount() > 0) {
            authInProcessLatch.await();
        }
    }

    @Before
    public void setUp() throws Exception {
        doReturn(Completable.complete()).when(mockRepository).addTrip(any(Trip.class));
        doReturn(Completable.complete()).when(mockRepository).addCoordinate(any(TripCoordinate.class), any(Trip.class));
        doReturn(Completable.complete()).when(mockRepository).updateTrip(any(Trip.class));
    }

    @Test
    public void shouldEmitStatistics_onNewLocationUpdate() throws Exception {
        TestObserver<TripStatistics> statisticsObserver = TestObserver.create();
        mViewModel.getTripStatisticsStreamObservable().subscribe(statisticsObserver);

        mViewModel.onActionButtonClicked();

        statisticsObserver.awaitCount(LOCATIONS_COUNT + 1, BaseTestConsumer.TestWaitStrategy.YIELD, TimeUnit.SECONDS.toMillis(1));

        // expecting locationsCount + 1 initial default statistics update
        assertEquals(LOCATIONS_COUNT + 1, statisticsObserver.valueCount());
    }

    @Test
    public void shouldEmitMapOptions_onNewLocationUpdate() throws Exception {
        TestObserver<MapOptions> mapOptionsObserver = TestObserver.create();
        mViewModel.getMapOptionsObservable().subscribe(mapOptionsObserver);

        mViewModel.onActionButtonClicked();

        mapOptionsObserver.awaitCount(LOCATIONS_COUNT, BaseTestConsumer.TestWaitStrategy.YIELD, TimeUnit.SECONDS.toMillis(1));

        assertEquals(LOCATIONS_COUNT, mapOptionsObserver.valueCount());
    }

    @Test
    public void shouldEmitGeolocationError_whenGeolocationUnavailable() throws Exception {
        TestObserver<Integer> snackbarMessageObserver = TestObserver.create();

        mViewModel.getShowSnackbarMessageBroadcast().subscribe(snackbarMessageObserver);

        mViewModel.onActionButtonClicked();

        viewModelInjectionRule.getInjectedLocationSource().onGeolocationAvailabilityChanged(false);

        snackbarMessageObserver.awaitCount(1, BaseTestConsumer.TestWaitStrategy.YIELD, TimeUnit.SECONDS.toMillis(1));

        snackbarMessageObserver.assertValue(R.string.message_geolocation_unavailable);
    }

    private List<Location> createLocationsList(int locationCount) {
        List<Location> locations = new ArrayList<>(locationCount);
        Random r = new Random();
        for (int i = 0; i < locationCount; i++) {
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
