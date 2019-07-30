package com.antonchaynikov.core.viewmodel;

import android.content.Context;

import com.antonchaynikov.core.R;
import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.utils.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Date;

import androidx.annotation.NonNull;

public class StatisticsFormatter {

    private static final int LARGEST_RELEVANT_DURATION_FIELD_INDEX = 3;

    private Context mAppContext;

    public StatisticsFormatter(@NonNull Context context) {
        mAppContext = context.getApplicationContext();
    }

    TripStatistics formatTrip(@NonNull Trip trip, @NonNull FormatOptions options) {
        TripStatistics statistics = new TripStatistics();
        statistics.setStartDate(formatStartDate(trip, options.getStartDatePattern()));
        statistics.setEndDate(formatEndDate(trip, options.getEndDatePattern()));
        statistics.setDuration(formatDuration(trip));
        statistics.setSpeed(formatSpeed(trip, options.getUnitSpeed()));
        statistics.setDistance(formatDistance(trip, options.getUnitDistance()));
        return statistics;
    }

    public TripStatistics formatTrip(@NonNull Trip trip) {
        return formatTrip(trip, new FormatOptions());
    }

    private String formatStartDate(Trip trip, String pattern) {
        return dateTimeFromMillis(trip.getStartDate()).toString(pattern);
    }

    private String formatEndDate(Trip trip, String pattern) {
        return dateTimeFromMillis(trip.getEndDate()).toString(pattern);
    }

    private String formatDuration(Trip trip) {
        DateTime startTime = dateTimeFromMillis(trip.getStartDate());
        DateTime endDate = dateTimeFromMillis(trip.getEndDate());
        Period period = new Period(startTime, endDate);
        int firstFieldIndex = getFirstNonEmptyFieldIndex(period);
        if (firstFieldIndex == -1) {
            return mAppContext.getString(R.string.statistics_duration_zero);
        }
        if (isTripDurationTooLong(firstFieldIndex)) {
            return mAppContext.getString(R.string.statistics_duration_eternity);
        }
        return generateDurationString(period);
    }

    private String formatSpeed(Trip trip, FormatOptions.UnitSpeed unit) {
        if (unit == FormatOptions.UnitSpeed.KMH) {
            String paramString = mAppContext.getString(R.string.statistics_speed_kmh);
            return String.format(paramString, StringUtils.numToFormattedString(trip.getSpeed() * 3.6, true));
        }
        String paramString = mAppContext.getString(R.string.statistics_speed_mps);
        return String.format(paramString, StringUtils.numToFormattedString(trip.getSpeed(), true));
    }

    private String formatDistance(Trip trip, FormatOptions.UnitDistance unit) {
        if (unit == FormatOptions.UnitDistance.KM) {
            String paramString = mAppContext.getString(R.string.statistics_distance_km);
            return String.format(paramString, StringUtils.numToFormattedString(trip.getDistance() / 1000, true));
        }
        String paramString = mAppContext.getString(R.string.statistics_distance_m);
        return String.format(paramString, StringUtils.numToFormattedString(trip.getDistance(), true));
    }

    private String generateDurationString(Period period) {
        return new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix("d")
                .appendSeparator(" ")
                .appendHours()
                .appendSuffix("h")
                .appendSeparator(" ")
                .appendMinutes()
                .appendSuffix("m")
                .appendSeparator(" ")
                .appendSeconds()
                .appendSuffix("s")
                .appendSeparator(" ").toFormatter().print(period);
    }

    private DateTime dateTimeFromMillis(long millis) {
        return new DateTime(new Date(millis));
    }

    private int getFirstNonEmptyFieldIndex(@NonNull Period period) {
        for (int i = 0; i < period.getFieldTypes().length; i++) {
            if (period.getValue(i) > 0) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean isTripDurationTooLong(int largestNonZeroFieldIndex) {
        return largestNonZeroFieldIndex < LARGEST_RELEVANT_DURATION_FIELD_INDEX;
    }
}
