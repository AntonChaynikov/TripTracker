package com.antonchaynikov.triptracker.mainscreen;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.RxImmediateSchedulerRule;
import com.antonchaynikov.triptracker.data.location.LocationSource;
import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.repository.Repository;
import com.antonchaynikov.triptracker.data.tripmanager.StatisticsCalculator;
import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter;
import com.antonchaynikov.triptracker.viewmodel.TripStatistics;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.test.platform.app.InstrumentationRegistry;
import io.reactivex.Completable;
import io.reactivex.observers.BaseTestConsumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class TripViewModelTripManagerIntegration {

    @ClassRule
    public static final RxImmediateSchedulerRule IMMEDIATE_SCHEDULER_RULE = new RxImmediateSchedulerRule();

    private static FirebaseAuth sFirebaseAuth;

    private TripViewModel mViewModel;

    @Mock
    private Repository mockRepository;
    @Mock
    private LocationSource mockLocationSource;

    private PublishSubject<Location> mLocationObservable = PublishSubject.create();
    private PublishSubject<Boolean> mGeolocationAvailabilityObservable = PublishSubject.create();

    @BeforeClass
    public static void setTestEnv() throws Exception {
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

        doReturn(Completable.complete()).when(mockRepository).addTrip(any(Trip.class));
        doReturn(Completable.complete()).when(mockRepository).addCoordinate(any(TripCoordinate.class), any(Trip.class));
        doReturn(Completable.complete()).when(mockRepository).updateTrip(any(Trip.class));

        doReturn(mLocationObservable).when(mockLocationSource).getLocationsObservable();
        doReturn(mGeolocationAvailabilityObservable).when(mockLocationSource).getGeolocationAvailabilityObservable();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        TripManager tripManager = new TripManager(mockRepository, mockLocationSource, new StatisticsCalculator());

        mViewModel = new TripViewModel(tripManager, sFirebaseAuth, new StatisticsFormatter(context), true);
    }

    @Test
    public void shouldEmitStatistics_onNewLocationUpdate() throws Exception {
        TestObserver<TripStatistics> statisticsObserver = TestObserver.create();
        mViewModel.getTripStatisticsStreamObservable().subscribe(statisticsObserver);

        mViewModel.onActionButtonClicked();

        int locationsCount = 2;
        for (Location location : createLocationsList(locationsCount)) {
            mLocationObservable.onNext(location);
        }

        statisticsObserver.awaitCount(locationsCount + 1, BaseTestConsumer.TestWaitStrategy.YIELD, TimeUnit.SECONDS.toMillis(1));

        // expecting locationsCount + 1 initial default statistics update
        assertEquals(locationsCount + 1, statisticsObserver.valueCount());
    }

    @Test
    public void shouldEmitMapOptions_onNewLocationUpdate() throws Exception {
        TestObserver<MapOptions> mapOptionsObserver = TestObserver.create();
        mViewModel.getMapOptionsObservable().subscribe(mapOptionsObserver);

        mViewModel.onActionButtonClicked();

        int itemsCount = 2;
        for (Location location : createLocationsList(itemsCount)) {
            mLocationObservable.onNext(location);
        }

        mapOptionsObserver.awaitCount(itemsCount, BaseTestConsumer.TestWaitStrategy.YIELD, TimeUnit.SECONDS.toMillis(1));

        assertEquals(itemsCount, mapOptionsObserver.valueCount());
    }

    @Test
    public void shouldEmitGeolocationError_whenGeolocationUnavailable() throws Exception {
        TestObserver<Integer> snackbarMessageObserver = TestObserver.create();

        mViewModel.getShowSnackbarMessageBroadcast().subscribe(snackbarMessageObserver);

        mViewModel.onActionButtonClicked();

        mGeolocationAvailabilityObservable.onNext(false);

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
