package com.antonchaynikov.triptracker.data.location;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LocationSource_LocationService_IntegrationTest {

    private static final int LOCATIONS_COUNT = 5;

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private TestableLocationProvider locationProvider;

    private LocationSource mLocationSource;

    private TestObserver<Location> mLocationsObserver;
    private TestObserver<Boolean> mGeolocationAvailabilityObserver;
    private List<Location> mLocationsList = createLocationsList();

    @Before
    public void setUp() throws Exception {
        mLocationsObserver = TestObserver.create();
        mGeolocationAvailabilityObserver = TestObserver.create();

        Context context = ApplicationProvider.getApplicationContext();
        locationProvider = LocationProviderModule.provideTestable(context);

        locationProvider.setTestMode();
        locationProvider.setFilter(null);

        mLocationSource = LocationSource.getInstance(context);
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

        locationProvider.setGeolocationAvailability(false);
        mGeolocationAvailabilityObserver.assertValue(false);

        locationProvider.setGeolocationAvailability(true);
        mGeolocationAvailabilityObserver.assertValues(false, true);
    }

    @Test
    public void startUpdates_shouldEmitGeolocationData() throws Exception {
        mLocationSource.getLocationsObservable().subscribe(mLocationsObserver);
        mLocationSource.startUpdates();

        locationProvider.emitLocations(mLocationsList);

        assertEquals(LOCATIONS_COUNT, mLocationsObserver.valueCount());
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
