package com.antonchaynikov.triptracker.data.model;

import androidx.annotation.Nullable;

public class TripCoordinate {

    public static final String FIELD_NAME_DATE = "date";
    public static final String FIELD_NAME_LATITUDE = "latitude";
    public static final String FIELD_NAME_LONGITUDE = "longitude";

    private double latitude;
    private double longitude;
    private double date;

    public TripCoordinate() {

    }

    public TripCoordinate(long date, double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDate() {
        return date;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDate(double date) {
        this.date = date;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof TripCoordinate) {
            TripCoordinate other = (TripCoordinate) o;
            return  other.date == this.date &&
                    other.latitude == this.latitude &&
                    other.longitude == this.longitude;
        }
        return false;
    }
}
