package com.antonchaynikov.triptracker.data.location;

import androidx.annotation.NonNull;

public interface Filter<T> {

    boolean isRelevant(@NonNull T element);

    void reset();
}
