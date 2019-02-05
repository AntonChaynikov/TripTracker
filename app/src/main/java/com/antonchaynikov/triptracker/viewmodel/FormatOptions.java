package com.antonchaynikov.triptracker.viewmodel;

import androidx.annotation.Nullable;

public class FormatOptions {

    public static final String DATE_FORMAT_DEFAULT = "dd.MM.yy HH:mm";
    public static final UnitSpeed UNIT_SPEED_DEFAULT = UnitSpeed.KMH;
    public static final UnitDistance UNIT_DISTANCE_DEFAULT = UnitDistance.KM;

    private String mStartDatePattern;
    private String mEndDatePattern;
    private UnitSpeed mUnitSpeed;
    private UnitDistance mUnitDistance;

    public String getStartDatePattern() {
        return mStartDatePattern == null ? DATE_FORMAT_DEFAULT : mStartDatePattern;
    }

    public String getEndDatePattern() {
        return mEndDatePattern == null ? getStartDatePattern() : mEndDatePattern;
    }

    public UnitSpeed getUnitSpeed() {
        return mUnitSpeed == null ? UNIT_SPEED_DEFAULT : mUnitSpeed;
    }

    public UnitDistance getUnitDistance() {
        return mUnitDistance == null ? UNIT_DISTANCE_DEFAULT : mUnitDistance;
    }

    public enum UnitSpeed {
        KMH, MPS
    }

    public enum UnitDistance {
        KM, M
    }

    public FormatOptions setStartDatePattern(@Nullable String pattern) {
        mStartDatePattern = pattern;
        return this;
    }

    public FormatOptions setEndDatePattern(String endDatePattern) {
        mEndDatePattern = endDatePattern;
        return this;
    }

    public FormatOptions setUnitSpeed(UnitSpeed unitSpeed) {
        mUnitSpeed = unitSpeed;
        return this;
    }

    public FormatOptions setUnitDistance(UnitDistance unitDistance) {
        mUnitDistance = unitDistance;
        return this;
    }
}
