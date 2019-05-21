package com.antonchaynikov.triptracker

import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.tripscreen.TripViewModelModule
import dagger.Component

@Component(dependencies = [NewTripFragmentComponent::class], modules = [TripViewModelModule::class])
interface NewTripFragmentComponent {
    fun inject(tripFragment: TripFragment)
}