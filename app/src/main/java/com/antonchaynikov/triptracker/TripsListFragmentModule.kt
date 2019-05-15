package com.antonchaynikov.triptracker

import com.antonchaynikov.tripslist.TripsListFragment
import com.antonchaynikov.tripslist.TripsListViewModelModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface TripsListFragmentModule {
    @ContributesAndroidInjector(modules = [TripsListViewModelModule::class])
    fun tripsListFragment(): TripsListFragment
}