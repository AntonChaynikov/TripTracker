package com.antonchaynikov.core.data.location

import android.content.Context
import com.antonchaynikov.core.data.location.LocationSource
import com.antonchaynikov.core.data.location.LocationSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [LocationProviderModule::class])
class LocationSourceModule {
    @Singleton
    @Provides
    fun locationSource(context: Context, locationProvider: LocationProvider): LocationSource {
        val locationSource = LocationSourceImpl.getInstance(context)
        locationSource.setLocationProvider(locationProvider)
        return LocationSourceImpl.getInstance(context)
    }
}

