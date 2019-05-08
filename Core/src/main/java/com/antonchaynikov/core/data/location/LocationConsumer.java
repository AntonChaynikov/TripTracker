package com.antonchaynikov.core.data.location;

import android.location.Location;

import androidx.annotation.NonNull;

interface LocationConsumer {

    void onNewLocationUpdate(@NonNull Location location);

    void onLocationUpdatesAvailabilityChange(boolean areUpdatesAvailable);

}
