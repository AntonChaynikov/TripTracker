package com.antonchaynikov.triptracker

import com.antonchaynikov.tripslist.TripsListFragment
import com.antonchaynikov.tripslist.TripsListScope
import com.antonchaynikov.tripslist.TripsListViewModelModule
import com.antonchaynikov.triptracker.application.AppComponent
import dagger.BindsInstance
import dagger.Component

@TripsListScope
@Component(dependencies = [AppComponent::class], modules = [TripsListViewModelModule::class])
interface TripListComponent {

    @Component.Builder
    interface Builder {
        fun build(): TripListComponent
        @BindsInstance
        fun fragment(fragment: TripsListFragment): Builder
        fun appComponent(appComponent: AppComponent): Builder
    }

    fun inject(tripsListFragment: TripsListFragment)
}