package com.antonchaynikov.triptracker.mainscreen

import android.content.Context
import com.antonchaynikov.triptracker.data.location.LocationSource
import com.antonchaynikov.triptracker.data.repository.Repository
import com.antonchaynikov.triptracker.data.tripmanager.StatisticsCalculator
import com.antonchaynikov.triptracker.data.tripmanager.TripManager
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
class MockTripViewModelModule constructor(
        val isLocationPermissionGranted: Boolean,
        val locationSource: LocationSource,
        val repository: Repository,
        val firebaseAuth: FirebaseAuth,
        context : Context) {

    private val mContext : Context

    init {
        mContext = context.applicationContext
    }

    @Provides
    internal fun provideViewModel(statisticsFormatter: StatisticsFormatter,
                                  tripManager: TripManager) : TripViewModel {
        return TripViewModel(
                tripManager,
                firebaseAuth,
                statisticsFormatter,
                isLocationPermissionGranted)
    }

    @Provides
    private fun provideStatisticsFormatter() = StatisticsFormatter(mContext)

    @Provides
    private fun provideStatisticsCalculator() = StatisticsCalculator()

    @Provides
    private fun provideTripManager(statisticsCalculator: StatisticsCalculator) =
            TripManager.getInstance(repository, locationSource, statisticsCalculator)
}