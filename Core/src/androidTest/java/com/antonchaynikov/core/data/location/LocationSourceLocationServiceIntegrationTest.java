package com.antonchaynikov.core.data.location;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LocationSourceLocationServiceIntegrationTest {

    private static final int LOCATIONS_COUNT = 5;

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private MockLocationProvider locationProvider;

    private LocationSourceImpl mLocationSource;

    private TestObserver<Location> mLocationsObserver;
    private TestObserver<Boolean> mGeolocationAvailabilityObserver;
    private PublishSubject<Location> mLocationsObservable = PublishSubject.create();

    @Before
    public void setUp() throws Exception {
        mLocationsObserver = TestObserver.create();
        mGeolocationAvailabilityObserver = TestObserver.create();

        Context context = ApplicationProvider.getApplicationContext();
        locationProvider = new MockLocationProvider(mLocationsObservable);

        mLocationSource = new LocationSourceImpl(context);
        mLocationSource.setLocationProvider(locationProvider);
        mLocationSource.setServiceConnectedSyncMode();
    }

    @After
    public void tearDown() {
        mLocationSource.finishUpdates();
    }

    @Test
    public void startUpdates_shouldEmitGeolocationAvailabilityEvents() throws Exception {
        mLocationSource.getGeolocationAvailabilityObservable().subscribe(mGeolocationAvailabilityObserver::onNext);
        mLocationSource.startUpdates();

        locationProvider.onGeolocationAvailabilityChanged(false);
        mGeolocationAvailabilityObserver.assertValue(false);

        locationProvider.onGeolocationAvailabilityChanged(true);
        mGeolocationAvailabilityObserver.assertValues(false, true);
    }

    @Test
    public void startUpdates_shouldEmitGeolocationData() throws Exception {
        mLocationSource.getLocationsObservable().subscribe(mLocationsObserver);
        mLocationSource.startUpdates();

        InstrumentationRegistry.getInstrumentation().waitForIdle(() -> {
            System.out.println("Idle now");
        });

        for (Location location : createLocationsList()) {
            mLocationsObservable.onNext(location);
        }

        Assert.assertEquals(LOCATIONS_COUNT, mLocationsObserver.valueCount());
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
