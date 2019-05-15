package com.antonchaynikov.core.data.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface LocationProvider {

    void startUpdates(@NonNull LocationConsumer consumer);

    void stopUpdates(@NonNull LocationConsumer consumer);

    void setFilter(@Nullable Filter<Location> filter);
}