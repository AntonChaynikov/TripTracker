package com.antonchaynikov.triptracker.viewmodel;

import androidx.annotation.Nullable;

class FormatOptions {

    static final String DATE_FORMAT_DEFAULT = "dd.MM.yy HH:mm";
    static final UnitSpeed UNIT_SPEED_DEFAULT = UnitSpeed.KMH;
    static final UnitDistance UNIT_DISTANCE_DEFAULT = UnitDistance.KM;

    private String mStartDatePattern;
    private String mEndDatePattern;
    private UnitSpeed mUnitSpeed;
    private UnitDistance mUnitDistance;

    String getStartDatePattern() {
        return mStartDatePattern == null ? DATE_FORMAT_DEFAULT : mStartDatePattern;
    }

    String getEndDatePattern() {
        return mEndDatePattern == null ? getStartDatePattern() : mEndDatePattern;
    }

    UnitSpeed getUnitSpeed() {
        return mUnitSpeed == null ? UNIT_SPEED_DEFAULT : mUnitSpeed;
    }

    UnitDistance getUnitDistance() {
        return mUnitDistance == null ? UNIT_DISTANCE_DEFAULT : mUnitDistance;
    }

    public enum UnitSpeed {
        KMH, MPS
    }

    public enum UnitDistance {
        KM, M
    }

    FormatOptions setStartDatePattern(@Nullable String pattern) {
        mStartDatePattern = pattern;
        return this;
    }

    FormatOptions setEndDatePattern(String endDatePattern) {
        mEndDatePattern = endDatePattern;
        return this;
    }

    FormatOptions setUnitSpeed(UnitSpeed unitSpeed) {
        mUnitSpeed = unitSpeed;
        return this;
    }

    FormatOptions setUnitDistance(UnitDistance unitDistance) {
        mUnitDistance = unitDistance;
        return this;
    }
}
