package com.antonchaynikov.triptracker.data.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class Trip {

    public static final String FIELD_NAME_START_DATE = "startDate";
    public static final String FIELD_NAME_END_DATE = "endDate";
    public static final String FIELD_NAME_COLLECTION_COORDINATES = "coordinates";

    @PropertyName(FIELD_NAME_START_DATE)
    private long startDate;
    @PropertyName(FIELD_NAME_END_DATE)
    private long endDate;

    // In meters
    @Exclude
    private double distance;
    // Speed in meters per second
    @Exclude
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
}
