package com.antonchaynikov.triptracker.data.location;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(includes = LocationProviderModule.class)
public class LocationSourceModule {

    @Provides
    public LocationSource provideLocationSource(Context context, LocationProvider locationProvider) {
        LocationSourceImpl locationSource = LocationSourceImpl.getInstance(context);
        locationSource.setLocationProvider(locationProvider);
        return LocationSourceImpl.getInstance(context);
    }
}
