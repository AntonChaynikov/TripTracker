package com.antonchaynikov.triptracker.application

import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.core.data.tripmanager.TripManager
import com.antonchaynikov.core.viewmodel.StatisticsFormatter
import com.antonchaynikov.tripscreen.TripViewModel
import com.antonchaynikov.tripshistory.HistoryViewModel
import com.antonchaynikov.tripslist.TripsListViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
open class TestViewModelsModule {
    @Provides
    fun tripViewModel(tripManager: TripManager,
                      firebaseAuth: FirebaseAuth,
                      statisticsFormatter: StatisticsFormatter): TripViewModel =
            TripViewModel(
                    tripManager,
                    firebaseAuth,
                    statisticsFormatter,
                    true)

    @Provides
    fun historyViewModel(repository: Repository,
                          statisticsFormatter: StatisticsFormatter): HistoryViewModel =
            HistoryViewModel(repository, statisticsFormatter, 123)

    @Provides
    fun tripsListViewModel(repository: Repository) : TripsListViewModel =
            TripsListViewModel(repository)
}