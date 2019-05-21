package com.antonchaynikov.triptracker

import com.antonchaynikov.tripshistory.HistoryFragment
import com.antonchaynikov.tripshistory.TripHistoryModule
import com.antonchaynikov.triptracker.application.NewAppComponent
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [NewAppComponent::class], modules = [TripHistoryModule::class])
interface NewHistoryComponent {
    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: NewAppComponent): Builder
        @BindsInstance
        fun historyFragment(historyFragment: HistoryFragment): Builder
        fun build(): NewHistoryComponent
    }

    fun inject(historyFragment: HistoryFragment)
}