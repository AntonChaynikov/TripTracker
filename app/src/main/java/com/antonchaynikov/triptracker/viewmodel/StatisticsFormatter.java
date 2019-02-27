package com.antonchaynikov.triptracker.viewmodel;

import android.content.Context;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.utils.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Date;

import androidx.annotation.NonNull;

public class StatisticsFormatter {

    private static final int LARGEST_RELEVANT_DURATION_FIELD_INDEX = 3;

    private Context mAppContext;
    private Trip mTrip;

    public StatisticsFormatter(@NonNull Context context) {
        mAppContext = context.getApplicationContext();
    }

    TripStatistics formatTrip(@NonNull Trip trip, @NonNull FormatOptions options) {
        mTrip = trip;
        TripStatistics statistics = new TripStatistics();
        statistics.setStartDate(formatStartDate(options.getStartDatePattern()));
        statistics.setEndDate(formatEndDate(options.getEndDatePattern()));
        statistics.setDuration(formatDuration());
        statistics.setSpeed(formatSpeed(options.getUnitSpeed()));
        statistics.setDistance(formatDistance(options.getUnitDistance()));
        return statistics;
    }

    public TripStatistics formatTrip(@NonNull Trip trip) {
        return formatTrip(trip, new FormatOptions());
    }

    private String formatStartDate(String pattern) {
        return dateTimeFromMillis(mTrip.getStartDate()).toString(pattern);
    }

    private String formatEndDate(String pattern) {
        return dateTimeFromMillis(mTrip.getEndDate()).toString(pattern);
    }

    private String formatDuration() {
        DateTime startTime = dateTimeFromMillis(mTrip.getStartDate());
        DateTime endDate = dateTimeFromMillis(mTrip.getEndDate());
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

    private String formatSpeed(FormatOptions.UnitSpeed unit) {
        if (unit == FormatOptions.UnitSpeed.KMH) {
            String paramString = mAppContext.getString(R.string.statistics_speed_kmh);
            return String.format(paramString, StringUtils.numToFormattedString(mTrip.getSpeed() * 3.6, true));
        }
        String paramString = mAppContext.getString(R.string.statistics_speed_mps);
        return String.format(paramString, StringUtils.numToFormattedString(mTrip.getSpeed(), true));
    }

    private String formatDistance(FormatOptions.UnitDistance unit) {
        if (unit == FormatOptions.UnitDistance.KM) {
            String paramString = mAppContext.getString(R.string.statistics_distance_km);
            return String.format(paramString, StringUtils.numToFormattedString(mTrip.getDistance() / 1000, true));
        }
        String paramString = mAppContext.getString(R.string.statistics_distance_m);
        return String.format(paramString, StringUtils.numToFormattedString(mTrip.getDistance(), true));
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
