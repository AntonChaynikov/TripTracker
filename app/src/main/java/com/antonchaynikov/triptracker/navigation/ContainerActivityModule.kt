package com.antonchaynikov.triptracker.navigation

import com.antonchaynikov.triptracker.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ContainerActivityModule {
    @ContributesAndroidInjector( modules = [
        NavigationModule::class,
        LoginFragmentModule::class,
        TripFragmentModule::class,
        HistoryFragmentModule::class,
        TripsListFragmentModule::class ])
    fun containerActivity(): ContainerActivity
}