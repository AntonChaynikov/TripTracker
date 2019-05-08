package com.antonchaynikov.triptracker.injection

import android.content.Context
import com.antonchaynikov.core.data.location.LocationSourceModule
import com.antonchaynikov.core.data.tripmanager.TripManagerModule

import com.antonchaynikov.core.authentication.AuthModule
import com.antonchaynikov.triptracker.history.DaggerHistoryComponent
import com.antonchaynikov.triptracker.history.HistoryFragment
import com.antonchaynikov.triptracker.history.HistoryModule
import com.antonchaynikov.triptracker.mainscreen.DaggerTripComponent
import com.antonchaynikov.triptracker.mainscreen.TripComponent
import com.antonchaynikov.triptracker.mainscreen.TripFragment
import com.antonchaynikov.triptracker.mainscreen.TripModule
import com.antonchaynikov.triptracker.trips.DaggerTripsListComponent
import com.antonchaynikov.triptracker.trips.TripsListFragment
import com.antonchaynikov.triptracker.trips.TripsListModule

object Injector {

    lateinit var appComponent: AppComponent

    @JvmStatic
    var isTestMode: Boolean = false

    var tripComponent: TripComponent? = null

    @JvmStatic
    fun init(context : Context) {
        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(context.applicationContext))
                .build()
    }

    @JvmStatic
    fun injectTripsListFragmentDependencies(fragment : TripsListFragment) {
        DaggerTripsListComponent.builder()
                .appComponent(appComponent)
                .tripsListModule(TripsListModule(fragment))
                .build()
                .inject(fragment)
    }

    @JvmStatic
    fun injectHistoryFragmentDependencies(fragment : HistoryFragment, tripStartDate : Long) {
        DaggerHistoryComponent.builder()
                .appComponent(appComponent)
                .historyModule(HistoryModule(fragment, tripStartDate))
                .commonViewModelModule(com.antonchaynikov.core.viewmodel.CommonViewModelModule())
                .build()
                .inject(fragment)
    }

    @JvmStatic
    fun injectTripFragmentDependencies(fragment : TripFragment, isLocationPermissionGranted : Boolean) {
        if (isTestMode && tripComponent != null) {
            tripComponent?.inject(fragment)
        } else {
            DaggerTripComponent.builder()
                    .appComponent(appComponent)
                    .authModule(AuthModule())
                    .commonViewModelModule(com.antonchaynikov.core.viewmodel.CommonViewModelModule())
                    .locationSourceModule(LocationSourceModule())
                    .tripManagerModule(TripManagerModule())
                    .tripModule(TripModule(fragment, isLocationPermissionGranted))
                    .build()
                    .inject(fragment)
        }
    }
}