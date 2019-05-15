package com.antonchaynikov.core.viewmodel;

import android.content.Context;

import com.antonchaynikov.core.R;
import com.antonchaynikov.core.data.model.Trip;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

public class StatisticsFormatterTest {

    private static final long START_DATE = 0;
    private static final long END_DATE = 3_000_000_000L;
    // in m/s
    private static final double SPEED = 2;
    // in meters
    private static final double DISTANCE = 2000;

    private StatisticsFormatter mTestSubject;
    @Mock
    private Context mockContext;

    private Trip mTrip;

    @Before
    public void setUp() throws Exception {
        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
        initMocks(this);
        doReturn(mockContext).when(mockContext).getApplicationContext();
        doReturn("Eternity").when(mockContext).getString(R.string.statistics_duration_eternity);
        doReturn("0s").when(mockContext).getString(R.string.statistics_duration_zero);
        doReturn("%s km").when(mockContext).getString(R.string.statistics_distance_km);
        doReturn("%s m").when(mockContext).getString(R.string.statistics_distance_m);
        doReturn("%s km/h").when(mockContext).getString(R.string.statistics_speed_kmh);
        doReturn("%s m/s").when(mockContext).getString(R.string.statistics_speed_mps);

        mTestSubject = new StatisticsFormatter(mockContext);

        mTrip = createTestTrip();
    }

    @Test
    public void shouldFormatStartDate() {
        FormatOptions options = new FormatOptions();
        String pattern = "yy.MM HH.mm";
        options.setStartDatePattern("yy.MM HH.mm");
        TripStatistics statistics = mTestSubject.formatTrip(mTrip, options);

        assertEquals(getFormattedString(START_DATE, pattern), statistics.getStartDate());
    }

    @Test
    public void shouldFormatEndDate() {
        FormatOptions options = new FormatOptions();
        String pattern = "yy.MM HH.mm";
        options.setEndDatePattern("yy.MM HH.mm");
        TripStatistics statistics = mTestSubject.formatTrip(mTrip, options);

        assertEquals(getFormattedString(END_DATE, pattern), statistics.getEndDate());
    }

    @Test
    public void shouldFormatDuration() {

        Trip trip = createTripWithDuration(2, 3, 4, 5);
        TripStatistics statistics = mTestSubject.formatTrip(trip);
        assertEquals("2d 3h 4m 5s", statistics.getDuration());

        trip = createTripWithDuration(4, 0, 4, 5);
        statistics = mTestSubject.formatTrip(trip);
        assertEquals("4d 4m 5s", statistics.getDuration());

        trip = createTripWithDuration(0, 4, 23, 12);
        statistics = mTestSubject.formatTrip(trip);
        assertEquals("4h 23m 12s", statistics.getDuration());

        trip = createTripWithDuration(0, 0, 23, 12);
        statistics = mTestSubject.formatTrip(trip);
        assertEquals("23m 12s", statistics.getDuration());

        trip = createTripWithDuration(0, 0, 0, 0);
        statistics = mTestSubject.formatTrip(trip);
        assertEquals("0s", statistics.getDuration());

        trip = createTripWithDuration(8, 0, 0, 0);
        statistics = mTestSubject.formatTrip(trip);
        assertEquals("Eternity", statistics.getDuration());
    }

    @Test
    public void shouldFormatSpeed() {
        FormatOptions options = new FormatOptions();
        TripStatistics statistics = mTestSubject.formatTrip(mTrip, options);
        assertEquals("7.20 km/h", statistics.getSpeed());

        options = new FormatOptions().setUnitSpeed(FormatOptions.UnitSpeed.MPS);
        statistics = mTestSubject.formatTrip(mTrip, options);
        assertEquals("2 m/s", statistics.getSpeed());
    }

    @Test
    public void shouldFormatDistance() {
        FormatOptions options = new FormatOptions();
        TripStatistics statistics = mTestSubject.formatTrip(mTrip, options);
        assertEquals("2 km", statistics.getDistance());

        options = new FormatOptions().setUnitDistance(FormatOptions.UnitDistance.M);
        statistics = mTestSubject.formatTrip(mTrip, options);
        assertEquals("2000 m", statistics.getDistance());
    }

    private Trip createTestTrip() {
        Trip trip = new Trip();
        trip.setStartDate(START_DATE);
        trip.setEndDate(END_DATE);
        trip.updateStatistics(DISTANCE, SPEED);
        return trip;
    }

    private Trip createTripWithDuration(int days, int hours, int mins, int secs) {
        Trip trip = new Trip();
        long startDate = DateTime.now().getMillis();
        long endDate = DateTime.now().plusDays(days).plusHours(hours).plusMinutes(mins).plusSeconds(secs).getMillis();
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        return trip;
    }

    private String getFormattedString(long date, String pattern) {
        return new DateTime(new Date(date)).toString(pattern);
    }

}