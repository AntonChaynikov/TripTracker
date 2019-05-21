package com.antonchaynikov.triptracker

import com.antonchaynikov.tripshistory.HistoryFragment
import com.antonchaynikov.tripshistory.TripHistoryModule
import com.antonchaynikov.triptracker.application.NewAppComponent
import dagger.Component

@Component(dependencies = [NewAppComponent::class], modules = [TripHistoryModule::class])
interface NewHistoryComponent {

    fun inject(historyFragment: HistoryFragment)
}