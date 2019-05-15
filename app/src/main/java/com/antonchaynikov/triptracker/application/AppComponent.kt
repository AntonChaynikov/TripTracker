package com.antonchaynikov.triptracker.application

import android.content.Context
import com.antonchaynikov.core.RepositoryModule
import com.antonchaynikov.core.TripManagerModule
import com.antonchaynikov.core.authentication.AuthModule
import com.antonchaynikov.core.data.location.LocationSourceModule
import com.antonchaynikov.core.viewmodel.CommonViewModelModule
import com.antonchaynikov.triptracker.TripActivityModule
import com.antonchaynikov.triptracker.navigation.ContainerActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [
    AndroidSupportInjectionModule::class,
    TripActivityModule::class,
    ContainerActivityModule::class,
    TripManagerModule::class,
    RepositoryModule::class,
    CommonViewModelModule::class,
    AuthModule::class
        ])
        @Singleton
        interface AppComponent {

            @Component.Builder
            interface Builder {
                @BindsInstance
                fun context(context: Context): Builder
                fun repositoryModule(repositoryModule: RepositoryModule): Builder
                fun locationSourceModule(locationSourceModule: LocationSourceModule): Builder
                fun build(): AppComponent
    }

    fun inject(application: TripApplication)
}