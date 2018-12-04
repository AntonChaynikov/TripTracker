package com.antonchaynikov.triptracker.data;

import android.content.ComponentName;
import android.location.Location;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlatformLocationSourceTest {

    private PlatformLocationSource mTestSubject;
    @Mock
    private Filter<Location> mockFilter;
    @Mock
    private LocationService mockLocationService;
    @Mock
    private LocationService.LocalServiceBinder mockBinder;
    @Mock
    private Location mockLocation;

    private PublishSubject<Location> locationsBroadcast = PublishSubject.create();

    @Before
    public void setUp() throws Exception {
        when(mockBinder.getLocationService()).thenReturn(mockLocationService);
        when(mockLocationService.startUpdates(any(Filter.class))).thenReturn(locationsBroadcast);
        when(mockLocationService.isUpdateAvailable()).thenReturn(true);
        mTestSubject = PlatformLocationSource.getLocationSource(mockFilter);
    }

    @After
    public void tearDown() throws Exception {
        mTestSubject.resetInstance();
    }

    @Test
    public void shouldCallLocationServiceStartUpdates_ifItIsAvailable() {
        mTestSubject.onServiceConnected(new ComponentName("foo", "bar"), mockBinder);
        mTestSubject.startTrip();
        verify(mockLocationService).startUpdates(mockFilter);
    }

    @Test
    public void shouldNotCallLocationServiceStartUpdates_ifItIsUnavailable() {
        // service isn't running
        mTestSubject.startTrip();

        // service is running but updates are unavailable
        mTestSubject.onServiceConnected(new ComponentName("foo", "bar"), mockBinder);
        when(mockLocationService.isUpdateAvailable()).thenReturn(false);
        mTestSubject.startTrip();
        verify(mockLocationService, never()).startUpdates(mockFilter);
    }

    @Test
    public void isLocationsUpdateAvailable_shouldBeTrue_ifLocationServiceIsAvailableAndUpdatesAreAvailable() {
        mTestSubject.onServiceConnected(new ComponentName("foo", "bar"), mockBinder);
        when(mockLocationService.isUpdateAvailable()).thenReturn(true);
        assert(mTestSubject.isLocationsUpdateAvailable());
    }

    @Test
    public void isLocationsUpdateAvailable_shouldBeFalse_ifLocationServiceIsUnavailableOrLocationUpdatesAreUnavailable() {
        // LocationService is unavailable
        assertFalse(mTestSubject.isLocationsUpdateAvailable());

        // LocationService is available but location updates aren't
        when(mockLocationService.isUpdateAvailable()).thenReturn(false);
        mTestSubject.onServiceConnected(new ComponentName("foo", "bar"), mockBinder);
        assertFalse(mTestSubject.isLocationsUpdateAvailable());
    }

    @Test
    public void isLocationsUpdateEnabled_shouldReturnTrue_ifUpdatesAreStarted() {
        mTestSubject.onServiceConnected(new ComponentName("foo", "bar"), mockBinder);
        mTestSubject.startTrip();

        assert(mTestSubject.isLocationsUpdateEnabled());
    }

    @Test
    public void isLocationsUpdateEnabled_shouldReturnFalse_ifUpdatesNotStarted() {
        assertFalse(mTestSubject.isLocationsUpdateEnabled());

        mTestSubject.onServiceConnected(new ComponentName("foo", "bar"), mockBinder);
        mTestSubject.startTrip();
        mTestSubject.finishTrip();

        assertFalse(mTestSubject.isLocationsUpdateEnabled());
    }

    @Test
    public void finishTrip_shouldCallLocationServiceToStopUpdating() {
        mTestSubject.onServiceConnected(new ComponentName("foo", "bar"), mockBinder);
        mTestSubject.startTrip();
        mTestSubject.finishTrip();

        verify(mockLocationService).stopUpdates();
    }

    @Test
    public void finishTrip_shouldResetLocationsFilter() {
        mTestSubject.onServiceConnected(new ComponentName("foo", "bar"), mockBinder);
        mTestSubject.startTrip();
        mTestSubject.finishTrip();

        verify(mockFilter).reset();
    }

    @Test
    public void finishTrip_shouldReturnTripWithLocations() {
        mTestSubject.onServiceConnected(new ComponentName("foo", "bar"), mockBinder);
        mTestSubject.startTrip();
        locationsBroadcast.onNext(mockLocation);
        Trip resultingTrip = mTestSubject.finishTrip();
        assertEquals(1, resultingTrip.getLocationsList().size());
        assertEquals(mockLocation, resultingTrip.getLocationsList().get(0));
    }

    @Test
    public void onServiceConnected_shouldSubscribeToLocationsBroadcast() {
        mTestSubject.onServiceConnected(new ComponentName("foo", "bar"), mockBinder);
        List<Location> elements = new ArrayList<>();
        mTestSubject.getLocationUpdates().subscribe(elements::add);
        mTestSubject.startTrip();
        locationsBroadcast.onNext(mockLocation);

        assertEquals(1, elements.size());
        assertEquals(mockLocation, elements.get(0));
    }

}