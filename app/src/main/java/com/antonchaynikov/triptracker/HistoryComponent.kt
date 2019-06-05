package com.antonchaynikov.triptracker

import com.antonchaynikov.tripshistory.HistoryFragment
import com.antonchaynikov.tripshistory.HistoryScope
import com.antonchaynikov.tripshistory.TripHistoryModule
import com.antonchaynikov.triptracker.application.AppComponent
import dagger.BindsInstance
import dagger.Component

@HistoryScope
@Component(dependencies = [AppComponent::class], modules = [TripHistoryModule::class])
interface HistoryComponent {
    fun inject(historyFragment: HistoryFragment)

    @Component.Builder
    interface Builder {
        fun build(): HistoryComponent
        @BindsInstance
        fun fragment(fragment: HistoryFragment): Builder
        fun appComponent(appComponent: AppComponent): Builder
    }
}