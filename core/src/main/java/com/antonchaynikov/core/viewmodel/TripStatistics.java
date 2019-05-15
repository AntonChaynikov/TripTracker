package com.antonchaynikov.core.viewmodel;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TripStatistics {
    private String mDistance;
    private String mSpeed;
    private String mStartDate;
    private String mEndDate;
    private String mDuration;

    TripStatistics() {}

    public TripStatistics(@NonNull String speed, @NonNull String distance) {
        mDistance = distance;
        mSpeed = speed;
    }

    public static TripStatistics getDefaultStatistics() {
        return new TripStatistics("0", "0");
    }

    public String getDistance() {
        return mDistance;
    }

    public String getSpeed() {
        return mSpeed;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public String getDuration() {
        return mDuration;
    }

    void setDistance(String distance) {
        mDistance = distance;
    }

    void setSpeed(String speed) {
        mSpeed = speed;
    }

    void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    void setDuration(String duration) {
        mDuration = duration;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o instanceof TripStatistics) {
            TripStatistics other = (TripStatistics) o;
            return other.mDistance.equals(mDistance) &&
                    other.mDuration.equals(mDuration) &&
                    other.mEndDate.equals(mEndDate) &&
                    other.mStartDate.equals(mStartDate) &&
                    other.mSpeed.equals(mSpeed);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDistance, mSpeed, mStartDate, mEndDate, mDuration);
    }
}
