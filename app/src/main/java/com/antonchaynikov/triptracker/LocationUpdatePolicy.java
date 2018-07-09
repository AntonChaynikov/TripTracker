package com.antonchaynikov.triptracker;

import android.location.Location;

public interface LocationUpdatePolicy {

    void updateLastLocationRecieved(Location location);

    Location getRelevantLocation();

}

