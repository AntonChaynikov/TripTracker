package com.antonchaynikov.triptracker.application

import com.antonchaynikov.tripshistory.HistoryFragment
import com.antonchaynikov.tripslist.TripsListFragment
import dagger.Module
import dagger.Provides

@Module
open class TestFragmentsModule {

    @Provides
    fun tripsListFragment(): TripsListFragment = TripsListFragment()

    @Provides
    fun historyFragment(): HistoryFragment = HistoryFragment()
}