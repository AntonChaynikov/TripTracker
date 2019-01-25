package com.antonchaynikov.triptracker.data.tripmanager;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class StatisticsCalculatorTest {

    private StatisticsCalculator mTestSubject;

    @Mock
    private Location loc1;
    @Mock
    private Location loc2;
    @Mock
    private Location loc3;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(10f).when(loc1).distanceTo(any(Location.class));
        doReturn(20f).when(loc2).distanceTo(any(Location.class));

        doReturn(0L).when(loc1).getTime();
        doReturn(1000L).when(loc2).getTime();
        doReturn(4000L).when(loc3).getTime();

        mTestSubject = new StatisticsCalculator();
    }

    @Test
    public void getDistance_shouldReturnCorrectDistance_whenLocationUpdated() {
        mTestSubject.addCoordinate(loc1);
        mTestSubject.addCoordinate(loc2);
        mTestSubject.addCoordinate(loc3);

        // Dist between 1 loc and 2 loc is 10m, between 2 loc and 3 loc is 20m
        assertEquals(30f, mTestSubject.getDistance(), 0.001);
    }

    @Test
    public void getDistance_shouldReturnCorrectSpeed_whenLocationUpdated() {
        mTestSubject.addCoordinate(loc1);
        // 1 sec between 1 loc and 2 loc
        mTestSubject.addCoordinate(loc2);
        // 3 sec between 2 loc and 3 loc
        mTestSubject.addCoordinate(loc3);

        // 30 meters per 4 seconds = 7.5 m/s
        assertEquals(7.5f, mTestSubject.getSpeed(), 0.001);
    }

    @Test
    public void reset_shouldResetDistance() {

        mTestSubject.addCoordinate(loc1);
        mTestSubject.addCoordinate(loc2);

        mTestSubject.reset();

        assertEquals(0f, mTestSubject.getDistance(), 0.001);
    }

    @Test
    public void reset_shouldResetCoordinatesData() {
        mTestSubject.addCoordinate(loc1);

        mTestSubject.reset();

        mTestSubject.addCoordinate(loc2);

        assertEquals(0f, mTestSubject.getDistance(), 0.001);
    }

    @Test
    public void reset_shouldResetTimeData() {
        mTestSubject.addCoordinate(loc1);

        mTestSubject.reset();

        mTestSubject.addCoordinate(loc2);
        mTestSubject.addCoordinate(loc3);

        // 20m distance / 3 sec duration ~= 6.667 m/s
        assertEquals(6.667f, mTestSubject.getSpeed(), 0.001);
    }
}