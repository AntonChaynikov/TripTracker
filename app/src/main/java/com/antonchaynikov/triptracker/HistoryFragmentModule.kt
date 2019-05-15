package com.antonchaynikov.triptracker

import com.antonchaynikov.tripshistory.HistoryFragment
import com.antonchaynikov.tripshistory.TripHistoryModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface HistoryFragmentModule {
    @ContributesAndroidInjector(modules = [TripHistoryModule::class])
    fun historyFragment(): HistoryFragment
}