package com.antonchaynikov.tripscreen;

import androidx.annotation.NonNull;

import com.antonchaynikov.core.data.model.TripCoordinate;

class MapOptions {

    private static final float MAP_ZOOM_LEVEL = 20;

    private TripCoordinate mCoordinates;
    private boolean mShouldDeleteMarkers;
    private boolean mShouldAddRoute;

    MapOptions(@NonNull TripCoordinate coordinates) {
        mCoordinates = coordinates;
    }

    MapOptions(boolean shouldDeleteMarkers) {
        mShouldDeleteMarkers = shouldDeleteMarkers;
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

    boolean shouldDeleteMarkers() {
        return mShouldDeleteMarkers;
    }
}
