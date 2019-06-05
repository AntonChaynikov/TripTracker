package com.antonchaynikov.core.data.location

import android.content.Context
import com.antonchaynikov.core.data.location.LocationSource
import com.antonchaynikov.core.data.location.LocationSourceImpl
import dagger.Module
import dagger.Provides

@Module(includes = [LocationProviderModule::class])
class LocationSourceModule {
    @Provides
    fun locationSource(context: Context, locationProvider: LocationProvider): LocationSource {
        val locationSource = LocationSourceImpl.getInstance(context)
        locationSource.setLocationProvider(locationProvider)
        return LocationSourceImpl.getInstance(context)
    }
}

