package com.antonchaynikov.triptracker

import com.antonchaynikov.tripslist.TripsListFragment
import com.antonchaynikov.tripslist.TripsListViewModelModule
import com.antonchaynikov.triptracker.application.NewAppComponent
import dagger.Component

@Component(dependencies = [NewAppComponent::class], modules = [TripsListViewModelModule::class])
interface NewTripsListFragment {
    fun inject(tripsListFragment: TripsListFragment)
}