package com.antonchaynikov.triptracker.data;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TripTest {

    private static final float DISTANCE_BETWEEN_LOCATIONS = 10f;

    @Mock
    private Location mockLocation;

    @Before
    public void setUp() {
        when(mockLocation.distanceTo(any(Location.class))).thenReturn(DISTANCE_BETWEEN_LOCATIONS);
    }

    @Test
    public void addLocation_shouldAddLocationToTheTrip() {
        Trip testSubject = Trip.beginNewTrip();
        testSubject.addLocation(mockLocation);
        assertEquals(1, testSubject.getLocationsList().size());
    }

    @Test
    public void getDistance_shouldReturnCorrectDistance() {
        Trip testSubject = Trip.beginNewTrip();
        testSubject.addLocation(mockLocation);
        testSubject.addLocation(mockLocation);
        testSubject.addLocation(mockLocation);
        assertEquals(20, testSubject.getDistance(), 0.01);
    }



}