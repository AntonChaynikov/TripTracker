package com.antonchaynikov.triptracker

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TripActivityModule {
    @ContributesAndroidInjector(modules = [
        NavigationModule::class,
        LoginFragmentModule::class,
        TripFragmentModule::class,
        HistoryFragmentModule::class,
        TripsListFragmentModule::class
    ])
    internal abstract fun tripActivity(): TripActivity
}