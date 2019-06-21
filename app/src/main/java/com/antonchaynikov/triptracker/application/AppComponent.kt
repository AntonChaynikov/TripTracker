package com.antonchaynikov.triptracker.application

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.antonchaynikov.core.RepositoryModule
import com.antonchaynikov.core.TripManagerModule
import com.antonchaynikov.core.authentication.AuthModule
import com.antonchaynikov.core.data.location.LocationSourceModule
import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.core.data.tripmanager.TripManager
import com.antonchaynikov.core.viewmodel.CommonViewModelModule
import com.antonchaynikov.core.viewmodel.StatisticsFormatter
import com.antonchaynikov.login.NavigationLogin
import com.antonchaynikov.tripscreen.NavigationTripScreen
import com.antonchaynikov.tripscreen.TripViewModel
import com.antonchaynikov.triptracker.*
import com.antonchaynikov.triptracker.navigation.TripTrackerNavigator
import com.google.firebase.auth.FirebaseAuth
import dagger.BindsInstance
import dagger.Component
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [
    SubcomponentsModule::class,
    TripManagerModule::class,
    RepositoryModule::class,
    CommonViewModelModule::class,
    AuthModule::class,
    NavigationModule::class
])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun repositoryModule(repositoryModule: RepositoryModule): Builder
        fun locationSourceModule(locationSourceModule: LocationSourceModule): Builder
        fun build(): AppComponent
    }

    fun navigationLogin(): NavigationLogin
    fun tripComponent(): TripComponent.Builder
    fun historyComponent(): HistoryComponent.Builder
    fun tripListComponent(): TripListComponent.Builder
    fun launchComponent(): LaunchComponent.Builder
}