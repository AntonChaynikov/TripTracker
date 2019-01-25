package com.antonchaynikov.triptracker.data.location;

import android.content.ComponentName;
import android.location.Location;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class LocationSourceTest {

    private LocationSource mTestSubject;

    @Mock
    private LocationService.LocationServiceBinder mockBinder;
    @Mock
    private LocationService mockLocationService;
    @Mock
    private ServiceManager<?> mockServiceManager;
    @Mock
    private Filter<Location> mockFilter;
    @Mock
    private Location mockLocation;

    private PublishSubject<Location> mLocationsStream = PublishSubject.create();
    private PublishSubject<Boolean> mGeolocationStatusStream = PublishSubject.create();

    private ComponentName mServiceComponentName = ComponentName.createRelative("pkg", "cls");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(mockLocationService).when(mockBinder).getLocationService();
        doReturn(mLocationsStream).when(mockLocationService).getLocationsStream();
        doReturn(mGeolocationStatusStream).when(mockLocationService).getGeolocationAvailabilityUpdatesBroadcast();
        mTestSubject = LocationSource.getInstance(mockFilter, mockServiceManager);
    }

    @After
    public void tearDown() {
        LocationSource.resetInstance();
    }

    @Test
    public void startUpdates_shouldStartLocationsService() {
        testSubject_startUpdates();

        verify(mockServiceManager).startLocationService(any(LocationSource.class));
    }

    @Test
    public void startUpdates_shouldRequestLocationService() {
        testSubject_startUpdates();

        verify(mockLocationService).startUpdates(any(Filter.class));
    }

    @Test
    public void finishUpdates_shouldRequestLocationService_ifAvailable() {
        testSubject_startUpdates();
        mTestSubject.finishUpdates();

        verify(mockLocationService).stopUpdates();
    }

    @Test
    public void finishUpdates_shouldNotRequestLocationService_ifNotAvailable() {
        mTestSubject.onServiceDisconnected(mServiceComponentName);
        mTestSubject.finishUpdates();

        verify(mockLocationService, never()).stopUpdates();
    }

    @Test
    public void finishUpdates_shouldStopLocationService() {
        testSubject_startUpdates();
        mTestSubject.finishUpdates();

        verify(mockServiceManager).stopLocationService(mTestSubject);
    }

    @Test
    public void shouldBroadcastLocation_whenUpdateReceived() {
        TestObserver<Location> locationsObserver = TestObserver.create();
        mTestSubject.getLocationUpdates().subscribe(locationsObserver);

        testSubject_startUpdates();

        mLocationsStream.onNext(mockLocation);

        assertEquals(mockLocation, locationsObserver.values().get(0));
    }

    @Test
    public void shouldBroadcastGeolocationAvailabilityUpdate_whenUpdateReceivedFromService() {
        TestObserver<Boolean> updatesObserver = TestObserver.create();
        mTestSubject.getGeolocationAvailabilityUpdates().subscribe(updatesObserver);
        testSubject_startUpdates();

        mGeolocationStatusStream.onNext(true);

        assertEquals(true, updatesObserver.values().get(0));
    }

    private void testSubject_startUpdates() {
        mTestSubject.startUpdates();
        mTestSubject.onServiceConnected(mServiceComponentName, mockBinder);
    }
}
