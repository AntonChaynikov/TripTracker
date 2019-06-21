package com.antonchaynikov.triptracker

import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.tripscreen.TripViewModel
import com.antonchaynikov.tripscreen.TripViewModelModule
import com.antonchaynikov.tripscreen.TripsScreenScope
import com.antonchaynikov.triptracker.application.AppComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent

@TripsScreenScope
@Subcomponent(modules = [TripViewModelModule::class])
interface TripComponent {
    fun inject(tripFragment: TripFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): TripComponent
        @BindsInstance
        fun fragment(fragment: TripFragment): Builder
    }
}