package com.antonchaynikov.triptracker

import com.antonchaynikov.tripshistory.HistoryFragment
import com.antonchaynikov.tripshistory.HistoryScope
import com.antonchaynikov.tripshistory.TripHistoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent

@HistoryScope
@Subcomponent(modules = [TripHistoryModule::class])
interface HistoryComponent {
    fun inject(historyFragment: HistoryFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): HistoryComponent
        @BindsInstance
        fun fragment(fragment: HistoryFragment): Builder
    }
}