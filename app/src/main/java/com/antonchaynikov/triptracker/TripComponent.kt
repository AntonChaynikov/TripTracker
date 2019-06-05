package com.antonchaynikov.triptracker

import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.tripscreen.TripViewModel
import com.antonchaynikov.tripscreen.TripViewModelModule
import com.antonchaynikov.triptracker.application.AppComponent
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [TripViewModelModule::class])
interface TripComponent {
    fun inject(tripFragment: TripFragment)
}