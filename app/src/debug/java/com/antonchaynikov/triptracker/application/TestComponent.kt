package com.antonchaynikov.triptracker.application

import android.content.Context
import com.antonchaynikov.core.RepositoryModule
import com.antonchaynikov.core.TripManagerModule
import com.antonchaynikov.core.authentication.AuthModule
import com.antonchaynikov.core.data.location.LocationSourceModule
import com.antonchaynikov.core.injection.IInjector
import com.antonchaynikov.core.viewmodel.CommonViewModelModule
import com.antonchaynikov.tripscreen.NavigationTripScreen
import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.tripscreen.TripViewModel
import com.antonchaynikov.triptracker.NavigationModule
import dagger.BindsInstance
import dagger.Component
import dagger.Provides

@Component(modules =[
    TripManagerModule::class,
    RepositoryModule::class,
    AuthModule::class,
    NavigationModule::class,
    CommonViewModelModule::class,
    TestViewModelsModule::class
])
interface TestComponent: IInjector {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun repositoryModule(repositoryModule: RepositoryModule): Builder
        fun locationSourceModule(locationSourceModule: LocationSourceModule): Builder
        fun tripManagerModule(tripManagerModule: TripManagerModule): Builder
        fun build(): TestComponent
    }

    fun tripViewModel(): TripViewModel

    fun navigation(): NavigationTripScreen

    fun inject(tripFragment: TripFragment)
}