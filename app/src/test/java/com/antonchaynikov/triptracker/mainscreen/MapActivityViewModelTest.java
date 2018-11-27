package com.antonchaynikov.triptracker.mainscreen;

import android.location.Location;

import com.antonchaynikov.triptracker.data.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

import static com.antonchaynikov.triptracker.mainscreen.MapActivityViewModel.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MapActivityViewModelTest {

    private MapActivityViewModel mTestSubject;

    @Mock
    private LocationSource mockLocationSource;

    private PublishSubject<Location> locationBroadcast;
    @Mock
    private Location mockLocation;

    @Before
    public void setUp() throws Exception {

        locationBroadcast = PublishSubject.create();

        // setup Location
        when(mockLocation.getLatitude()).thenReturn(51d);
        when(mockLocation.getLongitude()).thenReturn(15d);

        // setup LocationSource
        doAnswer(invocation -> {
            locationBroadcast.onNext(mockLocation);
            return null;
        }).when(mockLocationSource).startUpdates();

        when(mockLocationSource.isUpdateEnabled()).thenReturn(true);
        when(mockLocationSource.getLocationUpdates()).thenReturn(locationBroadcast);

        mTestSubject = new MapActivityViewModel(mockLocationSource,true);
    }

    @Test
    public void onStartTripButtonClick_shouldAskForLocationPermission_ifNotGranted() {

        // Location permission is absent
        mTestSubject.setLocationPermissionStatus(false);

        List<Boolean> events = new LinkedList<>();
        mTestSubject.getRequestLocationPermissionEventBroadcast()
                .subscribe(events::add);

        mTestSubject.onStartTripButtonClick();

        // 1 and only 1 event should be broadcasted
        assertEquals(1, events.size());
    }

    @Test
    public void onStartTripButtonClick_shouldNotAskForLocationPermission_ifGranted() {

        // Location permission is present
        mTestSubject.setLocationPermissionStatus(true);

        List<Boolean> events = new LinkedList<>();
        mTestSubject.getRequestLocationPermissionEventBroadcast()
                .subscribe(events::add);

        mTestSubject.onStartTripButtonClick();

        // 1 and only 1 event should be broadcasted
        assertEquals(0, events.size());
    }

    @Test
    public void onStartTripButtonClick_shouldBroadcastLocation_ifLocationPermissionGranted() {

        // Location permission is present
        mTestSubject.setLocationPermissionStatus(true);

        List<LatLng> events = new LinkedList<>();
        mTestSubject.getNewLocationEventBroadcast()
                .subscribe(events::add);

        mTestSubject.onStartTripButtonClick();

        // 1 and only 1 event should be broadcasted
        assertEquals(1, events.size());
        assertEquals(new LatLng(51d,15d), events.get(0));
    }

    @Test
    public void onStartTripButtonClick_shouldNotBroadcastLocation_ifLocationPermissionNotGranted() {

        // Location permission isn't granted
        mTestSubject.setLocationPermissionStatus(false);

        List<LatLng> events = new LinkedList<>();
        mTestSubject.getNewLocationEventBroadcast()
                .subscribe(events::add);

        mTestSubject.onStartTripButtonClick();

        // 1 and only 1 event should be broadcasted
        assertEquals(0, events.size());
    }

    @Test
    public void onStartTripButtonClick_shouldBroadcastOnLocationBroadcastStatusChangedEvent_ifLocationServiceAvailable() {

        // Location permission is present
        mTestSubject.setLocationPermissionStatus(true);

        when(mockLocationSource.isUpdateEnabled()).thenReturn(true);

        List<LocationBroadcastStatus> events = new LinkedList<>();
        mTestSubject.getOnLocationBroadcastStatusChangedEventBroadcast()
                .subscribe(events::add);

        mTestSubject.onStartTripButtonClick();

        // 1 and only 1 event should be broadcasted
        assertEquals(2, events.size());
        assertEquals(LocationBroadcastStatus.BROADCASTING, events.get(1));
    }

    @Test
    public void onStartTripButtonClick_shouldNotBroadcastOnLocationBroadcastStatusChangedEvent_ifLocationServiceUnavailable() {

        // Location permission is present
        mTestSubject.setLocationPermissionStatus(true);

        when(mockLocationSource.isUpdateEnabled()).thenReturn(false);

        List<LocationBroadcastStatus> events = new LinkedList<>();
        mTestSubject.getOnLocationBroadcastStatusChangedEventBroadcast()
                .subscribe(events::add);

        mTestSubject.onStartTripButtonClick();

        // 1 and only 1 event should be broadcasted
        assertEquals(1, events.size());
        assertEquals(LocationBroadcastStatus.IDLE, events.get(0));
    }



}