package com.antonchaynikov.core.data.model;

import androidx.annotation.Keep;

import com.google.firebase.firestore.PropertyName;

import java.util.Objects;

@Keep
public class Trip {

    public static final String FIELD_NAME_START_DATE = "startDate";
    private static final String FIELD_NAME_END_DATE = "endDate";
    private static final String FIELD_NAME_DISTANCE = "distance";
    private static final String FIELD_NAME_SPEED = "speed";

    @PropertyName(FIELD_NAME_START_DATE)
    private long startDate;
    @PropertyName(FIELD_NAME_END_DATE)
    private long endDate;
    // In meters
    @PropertyName(FIELD_NAME_DISTANCE)
    private double distance;
    // Speed in meters per second
    @PropertyName(FIELD_NAME_SPEED)
    private double speed;

    public Trip() {
    }

    public Trip(long startDate) {
        this.startDate = startDate;
    }

    public Trip updateStatistics(double distance, double speed) {
        this.distance = distance;
        this.speed = speed;
        return this;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip)) return false;
        Trip trip = (Trip) o;
        return startDate == trip.startDate &&
                endDate == trip.endDate &&
                Double.compare(trip.distance, distance) == 0 &&
                Double.compare(trip.speed, speed) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, distance, speed);
    }
}
