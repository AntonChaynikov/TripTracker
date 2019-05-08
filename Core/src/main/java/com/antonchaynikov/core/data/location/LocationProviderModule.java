package com.antonchaynikov.core.data.location;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.Module;
import dagger.Provides;

@Module
public class LocationProviderModule {

    @Provides
    public LocationProvider provide(@NonNull Context context, @Nullable Filter<Location> locationFilter) {
        LocationProvider locationProvider = new LocationProviderPlatform(context);
        locationProvider.setFilter(locationFilter);
        return locationProvider;
    }

    @Provides
    public Filter<Location> provideLocationFilter() {
        return new LocationFilter();
    }
}
