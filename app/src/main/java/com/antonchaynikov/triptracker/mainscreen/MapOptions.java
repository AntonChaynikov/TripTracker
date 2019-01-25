package com.antonchaynikov.triptracker.mainscreen;

import com.antonchaynikov.triptracker.data.model.TripCoordinate;

import androidx.annotation.NonNull;

class MapOptions {

    private static final float MAP_ZOOM_LEVEL = 20;

    private TripCoordinate mCoordinates;
    private boolean mShouldDeleteMarkers;

    MapOptions(@NonNull TripCoordinate coordinates) {
        mCoordinates = coordinates;
    }

    MapOptions(boolean shouldDeleteMarkers) {
        mShouldDeleteMarkers = shouldDeleteMarkers;
    }

    double getCoordinatesLatitude() {
        return mCoordinates == null ? -1 : mCoordinates.getLatitude();
    }

    double getCoordinatesLongitude() {
        return mCoordinates == null ? -1 : mCoordinates.getLongitude();
    }

    float getCameraZoomLevel() {
        return MAP_ZOOM_LEVEL;
    }

    boolean shouldDeleteMarkers() {
        return mShouldDeleteMarkers;
    }
}
