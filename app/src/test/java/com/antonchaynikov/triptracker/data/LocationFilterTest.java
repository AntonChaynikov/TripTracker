package com.antonchaynikov.triptracker.data;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationFilterTest {

    private static final float TEST_ACCURACY_MARGIN = LocationFilter.DEFAULT_ACCURACY;
    private static final float TEST_VELOCITY_MARGIN = LocationFilter.DEFAULT_VELOCITY_LIMIT;
    private static final float TEST_DISTANCE_MARGIN = LocationFilter.DEFAULT_DISTANCE_MARGIN;

    private LocationFilter testSubject;
    @Mock
    private Location mockLocation;
    @Mock
    private Location newLocation;

    @Before
    public void setUp() throws Exception {
        when(mockLocation.getAccuracy()).thenReturn(TEST_ACCURACY_MARGIN);
        when(mockLocation.getTime()).thenReturn(0L);
        when(newLocation.getTime()).thenReturn(1000L);
        when(mockLocation.distanceTo(any(Location.class))).thenReturn(TEST_DISTANCE_MARGIN);
        testSubject = new LocationFilter(TEST_ACCURACY_MARGIN, TEST_VELOCITY_MARGIN, TEST_DISTANCE_MARGIN);
        testSubject.isRelevant(mockLocation);
    }

    @Test
    public void isRelevant_shouldReturnTrue_ifFilterIsEmpty() throws Exception {
        testSubject = new LocationFilter();
        assertTrue(testSubject.isRelevant(mockLocation));
    }

    @Test
    public void isRelevant_shouldReturnFalse_ifLocAccuracyIsLowerThenRequired() throws Exception {
        when(newLocation.getAccuracy()).thenReturn(TEST_ACCURACY_MARGIN + 1);
        assertFalse(testSubject.isRelevant(newLocation));
    }

    @Test
    public void calcDistance_shouldReturnCorrectResult() throws Exception {
        float expectedSpeed = 36;
        when(newLocation.getTime()).thenReturn(1000L);
        when(mockLocation.distanceTo(any(Location.class))).thenReturn(10f);
        assertEquals(expectedSpeed, testSubject.calcSpeed(newLocation), 0.1);
    }
    @Test
    public void isRelevant_shouldReturnFalse_ifVelocityIsHigherThanLimit() throws Exception {
        when(mockLocation.distanceTo(any(Location.class))).thenReturn(TEST_DISTANCE_MARGIN); // ~36 km/h
        when(newLocation.getTime()).thenReturn(550L);
        assertFalse(testSubject.isRelevant(newLocation));
    }

    @Test
    public void isRelevant_shouldReturnFalse_ifNewLocationUpdateIsOlderThanThePrev() throws Exception {
        when(mockLocation.getTime()).thenReturn(1L);
        when(newLocation.getTime()).thenReturn(0L);
        assertFalse(testSubject.isRelevant(newLocation));
    }

    @Test
    public void isRelevant_shouldReturnFalse_ifNewLocationsDistanceIsSmallerThanDistanceMargin() {
        when(mockLocation.distanceTo(any(Location.class))).thenReturn(TEST_DISTANCE_MARGIN - 1);
        when(newLocation.getTime()).thenReturn(300L);
        assertFalse(testSubject.isRelevant(newLocation));
    }

    @Test
    public void isRelevant_shouldReturnTrue_ifRelevant() {
        when(newLocation.getTime()).thenReturn(10000L);
        assertTrue(testSubject.isRelevant(newLocation));
    }

}