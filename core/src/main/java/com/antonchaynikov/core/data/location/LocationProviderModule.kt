package com.antonchaynikov.core.data.location

import android.content.Context
import android.location.Location
import dagger.Module
import dagger.Provides

//TODO return platform or servicesApi source depending on name parameter
@Module
open class LocationProviderModule {

    @Provides
    fun provide(context: Context, locationFilter: Filter<Location>): LocationProvider {
        val locationProvider = LocationProviderPlatform(context)
        locationProvider.setFilter(locationFilter)
        return locationProvider
    }

    @Provides
    fun provideLocationFilter(): Filter<Location> {
        return LocationFilter()
    }
}