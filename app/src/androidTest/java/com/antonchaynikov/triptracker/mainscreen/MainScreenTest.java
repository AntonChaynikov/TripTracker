package com.antonchaynikov.triptracker.mainscreen;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import com.antonchaynikov.triptracker.data.location.LocationSource;
import com.antonchaynikov.triptracker.data.location.MockLocationProvider;
import com.antonchaynikov.triptracker.data.repository.Repository;
import com.antonchaynikov.triptracker.data.repository.firestore.FireStoreDB;
import com.antonchaynikov.triptracker.data.tripmanager.StatisticsCalculator;
import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter;
import com.antonchaynikov.triptracker.viewmodel.TripStatistics;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;

public class MainScreenTest {

    /*

    private TripViewModel mViewModel;
    private FirebaseAuth mFirebaseAuth;
    private Repository mRepository;

    private MockLocationProvider mLocationProvider;
    private PublishSubject<Location> mLocationsStream = PublishSubject.create();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Authenticate as a test user
        mFirebaseAuth = FirebaseAuth.getInstance();
        CountDownLatch authInProcessLatch = new CountDownLatch(1);
        mFirebaseAuth.signInWithEmailAndPassword("test@test.test", "123456").addOnCompleteListener(t -> authInProcessLatch.countDown());
        while(authInProcessLatch.getCount() > 0) {
            authInProcessLatch.await();
        }
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        LocationSource locationSource = LocationSource.getInstance(context);
        locationSource.setServiceConnectedSyncMode();

        mLocationProvider = new MockLocationProvider(mLocationsStream);
        locationSource.setLocationProvider(mLocationProvider);

        mRepository = FireStoreDB.getInstance();

        TripManager tripManager = TripManager.getInstance(mRepository, locationSource, new StatisticsCalculator());

        mViewModel = new TripViewModel(tripManager, mFirebaseAuth, new StatisticsFormatter(context), true);
    }

    @After
    public void tearDown() {
        mRepository.deleteUserData().blockingAwait();
    }

    @Test
    public void shouldEmitFormattedStatistics_onNewLocationUpdate() throws Exception {
        TestObserver<TripStatistics> statisticsObserver = TestObserver.create();
        mViewModel.getTripStatisticsStreamObservable().subscribe(statisticsObserver);
        int locationsCount = 3;

        new Thread(() -> {
            mViewModel.onActionButtonClicked();

            for (Location location: createLocationsList(locationsCount)) {
                mLocationsStream.onNext(location);
            }
        }).start();

        assertEquals(locationsCount, statisticsObserver.valueCount());
        TimeUnit.SECONDS.sleep(6);
    }

    private List<Location> createLocationsList(int locationsCount) {
        List<Location> locations = new ArrayList<>(locationsCount);
        Random r = new Random();
        for (int i = 0; i < locationsCount; i++) {
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
    */
}