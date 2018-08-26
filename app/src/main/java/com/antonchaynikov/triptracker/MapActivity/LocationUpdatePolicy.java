package com.antonchaynikov.triptracker.MapActivity;

import android.location.Location;

public interface LocationUpdatePolicy {

    void updateLastLocationRecieved(Location location);

    Location getRelevantLocation();

}

