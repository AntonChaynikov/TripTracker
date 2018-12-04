package com.antonchaynikov.triptracker.data;

import android.location.Location;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;

public class Trip {

    private List<Location> mLocationsList;
    private double mDistance;

    private Trip() {
        mLocationsList = new LinkedList<>();
    }

    public static Trip beginNewTrip() {
        return new Trip();
    }

    public void addLocation(@NonNull Location location) {

    }

    public List<Location> getLocationsList() {
        return mLocationsList;
    }

    public double getDistance() {
        return mDistance;
    }
}
