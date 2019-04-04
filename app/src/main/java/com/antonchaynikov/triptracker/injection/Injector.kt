package com.antonchaynikov.triptracker.injection

import android.content.Context

//import com.antonchaynikov.triptracker.application.DaggerAppComponent
import com.antonchaynikov.triptracker.authentication.AuthModule
import com.antonchaynikov.triptracker.data.location.LocationSourceModule
import com.antonchaynikov.triptracker.data.tripmanager.TripManagerModule
//import com.antonchaynikov.triptracker.history.DaggerHistoryComponent
import com.antonchaynikov.triptracker.history.HistoryFragment
import com.antonchaynikov.triptracker.history.HistoryModule
//import com.antonchaynikov.triptracker.mainscreen.DaggerTripComponent
import com.antonchaynikov.triptracker.mainscreen.TripFragment
import com.antonchaynikov.triptracker.mainscreen.TripModule
//import com.antonchaynikov.triptracker.trips.DaggerTripsListComponent
import com.antonchaynikov.triptracker.trips.TripsListFragment
import com.antonchaynikov.triptracker.trips.TripsListModule
import com.antonchaynikov.triptracker.viewmodel.CommonViewModelModule

object Injector {

    lateinit var appComponent: AppComponent

    @JvmStatic
    fun init(context : Context) {
        /*
        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(context.applicationContext))
                .build()
                */
    }

    @JvmStatic
    fun injectTripsListFragmentDependencies(fragment : TripsListFragment) {
//        DaggerTripsListComponent.builder()
//                .appComponent(appComponent)
//                .tripsListModule(TripsListModule(fragment))
//                .build()
//                .inject(fragment)
    }

    @JvmStatic
    fun injectHistoryFragmentDependencies(fragment : HistoryFragment, tripStartDate : Long) {
//        DaggerHistoryComponent.builder()
//                .appComponent(appComponent)
//                .historyModule(HistoryModule(fragment, tripStartDate))
//                .commonViewModelModule(CommonViewModelModule())
//                .build()
//                .inject(fragment)
    }

    @JvmStatic
    fun injectTripFragmentDependencies(fragment : TripFragment, isLocationPermissionGranted : Boolean) {
//        DaggerTripComponent.builder()
//                .appComponent(appComponent)
//                .authModule(AuthModule())
//                .commonViewModelModule(CommonViewModelModule())
//                .locationSourceModule(LocationSourceModule())
//                .tripManagerModule(TripManagerModule())
//                .tripModule(TripModule(fragment, isLocationPermissionGranted))
//                .build()
//                .inject(fragment)
    }
}