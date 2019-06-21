package com.antonchaynikov.tripscreen;

import com.antonchaynikov.core.viewmodel.TripStatistics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TripStatisticsTest {

    @Test
    public void getDefaultStatistics() {
        TripStatistics tripStatistics = TripStatistics.getDefaultStatistics();
        assertEquals("0", tripStatistics.getDistance());
        assertEquals("0", tripStatistics.getSpeed());
    }
}