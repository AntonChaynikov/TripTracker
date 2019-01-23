package com.antonchaynikov.triptracker.mainscreen;

import androidx.annotation.NonNull;

class TripStatistics {
    private String mDistance;
    private String mSpeed;

    TripStatistics(@NonNull String speed, @NonNull String distance) {
        mDistance = distance;
        mSpeed = speed;
    }

    public String getDistance() {
        return mDistance;
    }

    public String getSpeed() {
        return mSpeed;
    }
}
