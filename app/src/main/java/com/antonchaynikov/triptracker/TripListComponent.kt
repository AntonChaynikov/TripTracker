package com.antonchaynikov.triptracker

import com.antonchaynikov.tripslist.TripsListFragment
import com.antonchaynikov.tripslist.TripsListViewModelModule
import com.antonchaynikov.triptracker.application.AppComponent
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [TripsListViewModelModule::class])
interface TripListComponent {
    fun inject(tripsListFragment: TripsListFragment)
}