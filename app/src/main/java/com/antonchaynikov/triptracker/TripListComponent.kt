package com.antonchaynikov.triptracker

import com.antonchaynikov.tripslist.TripsListFragment
import com.antonchaynikov.tripslist.TripsListScope
import com.antonchaynikov.tripslist.TripsListViewModelModule
import dagger.BindsInstance
import dagger.Subcomponent

@TripsListScope
@Subcomponent(modules = [TripsListViewModelModule::class])
interface TripListComponent {

    @Subcomponent.Builder
    interface Builder {
        fun build(): TripListComponent
        @BindsInstance
        fun fragment(fragment: TripsListFragment): Builder
    }

    fun inject(tripsListFragment: TripsListFragment)
}