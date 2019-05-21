package com.antonchaynikov.core

import com.antonchaynikov.core.data.location.LocationSource
import com.antonchaynikov.core.data.location.LocationSourceModule
import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.core.data.tripmanager.StatisticsCalculator
import com.antonchaynikov.core.data.tripmanager.TripManager
import dagger.Module
import dagger.Provides

@Module(includes = [LocationSourceModule::class])
open class TripManagerModule {

    @Provides
    fun tripManager(
            repository: Repository,
            locationSource: LocationSource,
            statisticsCalculator: StatisticsCalculator): TripManager = TripManager(repository, locationSource, statisticsCalculator)

    @Provides
    fun statisticsCalculator(): StatisticsCalculator = StatisticsCalculator()
}