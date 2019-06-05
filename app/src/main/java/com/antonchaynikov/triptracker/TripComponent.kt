package com.antonchaynikov.triptracker

import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.tripscreen.TripViewModelModule
import com.antonchaynikov.tripscreen.TripsScreenScope
import com.antonchaynikov.triptracker.application.AppComponent
import dagger.BindsInstance
import dagger.Component

@TripsScreenScope
@Component(dependencies = [AppComponent::class], modules = [TripViewModelModule::class])
interface TripComponent {
    fun inject(tripFragment: TripFragment)

    @Component.Builder
    interface Builder {
        fun build(): TripComponent
        fun appComponent(appComponent: AppComponent): Builder
        @BindsInstance
        fun fragment(fragment: TripFragment): Builder
    }
}