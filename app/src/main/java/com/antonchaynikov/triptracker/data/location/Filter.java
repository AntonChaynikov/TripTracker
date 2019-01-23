package com.antonchaynikov.triptracker.data.location;

import android.location.Location;

import androidx.annotation.NonNull;

public interface Filter<T> {
    boolean isRelevant(@NonNull T element);
    void reset();
}
