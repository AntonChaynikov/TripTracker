package com.antonchaynikov.triptracker.application

import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.tripshistory.HistoryFragment
import com.antonchaynikov.tripslist.TripsListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface TestFragmentsBindingModule {
    @ContributesAndroidInjector
    fun contributeTripFragment(): TripFragment

    @ContributesAndroidInjector
    fun contributeHistoryFragment(): HistoryFragment

    @ContributesAndroidInjector
    fun contributeTripsListFragment(): TripsListFragment
}