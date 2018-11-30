package com.antonchaynikov.triptracker.data;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlatformLocationSourceTest {

    private PlatformLocationSource mTestSubject;
    @Mock
    private LocationManager mockLocationManager;
    @Mock
    private Location mockLocation;
    @Mock
    private Filter<Location> mockFilter;

    @Before
    public void setUp() throws Exception {
        doNothing().when(mockLocationManager).requestLocationUpdates(anyString(), anyLong(), anyFloat(), any(LocationListener.class));
        doNothing().when(mockLocationManager).removeUpdates(any(LocationListener.class));
        when(mockLocationManager.isProviderEnabled(anyString())).thenReturn(true);
        when(mockFilter.isRelevant(any(Location.class))).thenReturn(true);
        mTestSubject = (PlatformLocationSource) PlatformLocationSource.getLocationSource(mockLocationManager, mockFilter);
    }

    @After
    public void tearDown() throws Exception {
        mTestSubject.resetInstance();
    }

    @Test
    public void startUpdates_shouldAskGPSandNetworkProvidersForUpdates() throws Exception {
        mTestSubject.startUpdates();
        verify(mockLocationManager).requestLocationUpdates(same(LocationManager.GPS_PROVIDER), anyLong(), anyFloat(), same(mTestSubject));
        verify(mockLocationManager).requestLocationUpdates(same(LocationManager.NETWORK_PROVIDER), anyLong(), anyFloat(), same(mTestSubject));
    }

    @Test
    public void stopUpdates_shouldRemoveUpdatesFromLocationManager() throws Exception {
        mTestSubject.stopUpdates();
        verify(mockLocationManager).removeUpdates(same(mTestSubject));
    }

    @Test
    public void isUpdateEnabled_shouldCheckWhetherOneOfTheProvidersIsEnabled() throws Exception {
        when(mockLocationManager.isProviderEnabled(anyString())).thenReturn(true);
        mTestSubject.isUpdateAvailable();
        assertTrue(mTestSubject.isUpdateAvailable());

        // if only network provider is enabled
        when(mockLocationManager.isProviderEnabled(same(LocationManager.GPS_PROVIDER))).thenReturn(false);
        assertTrue(mTestSubject.isUpdateAvailable());

        // if there are no providers that are enabled
        when(mockLocationManager.isProviderEnabled(anyString())).thenReturn(false);
        assertFalse(mTestSubject.isUpdateAvailable());

        // if only gps provider is enabled
        when(mockLocationManager.isProviderEnabled(same(LocationManager.GPS_PROVIDER))).thenReturn(true);
        assertTrue(mTestSubject.isUpdateAvailable());
    }

    @Test
    public void shouldBroadcastLocation_whenItsAvailable() throws Exception {
        List<Location> locations = new LinkedList<>();
        mTestSubject.getLocationUpdates()
                .subscribe(locations::add);
        mTestSubject.onLocationChanged(mockLocation);
        assertEquals(1, locations.size());
        assertEquals(mockLocation, locations.get(0));
    }

    @Test
    public void shouldNotBroadcastLocation_ifItsFilteredOut() throws Exception {
        when(mockFilter.isRelevant(any(Location.class))).thenReturn(false);
        List<Location> locations = new LinkedList<>();
        mTestSubject.getLocationUpdates()
                .subscribe(locations::add);
        mTestSubject.onLocationChanged(mockLocation);
        assertEquals(0, locations.size());
    }
}