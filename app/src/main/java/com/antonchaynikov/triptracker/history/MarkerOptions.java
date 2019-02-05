package com.antonchaynikov.triptracker.history;

import com.antonchaynikov.triptracker.data.model.TripCoordinate;

import java.util.Objects;

import androidx.annotation.NonNull;

public class MarkerOptions {

    private static final float MAP_ZOOM_LEVEL = 20;

    private TripCoordinate mCoordinates;

    public MarkerOptions(@NonNull TripCoordinate lastCoordinate) {
        mCoordinates = lastCoordinate;
    }

    double getCoordinatesLatitude() {
        return mCoordinates == null ? Double.NaN : mCoordinates.getLatitude();
    }

    double getCoordinatesLongitude() {
        return mCoordinates == null ? Double.NaN : mCoordinates.getLongitude();
    }

    float getCameraZoomLevel() {
        return MAP_ZOOM_LEVEL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarkerOptions)) return false;
        MarkerOptions that = (MarkerOptions) o;
        return Objects.equals(mCoordinates, that.mCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mCoordinates);
    }
}
