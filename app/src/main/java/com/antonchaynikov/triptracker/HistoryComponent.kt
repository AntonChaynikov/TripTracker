package com.antonchaynikov.triptracker

import com.antonchaynikov.tripshistory.HistoryFragment
import com.antonchaynikov.tripshistory.TripHistoryModule
import com.antonchaynikov.triptracker.application.AppComponent
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [TripHistoryModule::class])
interface HistoryComponent {
    fun inject(historyFragment: HistoryFragment)
}