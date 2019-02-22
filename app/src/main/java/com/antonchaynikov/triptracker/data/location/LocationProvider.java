package com.antonchaynikov.triptracker.data.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

interface LocationProvider {
    void startUpdates(@NonNull LocationConsumer consumer);
    void stopUpdates(@NonNull LocationConsumer consumer);
    void setFilter(@Nullable Filter<Location> filter);
}
