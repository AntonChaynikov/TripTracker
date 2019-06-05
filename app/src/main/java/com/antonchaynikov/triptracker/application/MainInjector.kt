package com.antonchaynikov.triptracker.application

import android.content.Context
import androidx.fragment.app.Fragment
import com.antonchaynikov.core.injection.IInjector
import com.antonchaynikov.login.LaunchFragment
import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.tripshistory.HistoryFragment
import com.antonchaynikov.tripslist.TripsListFragment
import com.antonchaynikov.triptracker.DaggerHistoryComponent
import com.antonchaynikov.triptracker.DaggerLaunchComponent
import com.antonchaynikov.triptracker.DaggerTripComponent
import com.antonchaynikov.triptracker.DaggerTripListComponent

object MainInjector: IInjector {

    private lateinit var appComponent: AppComponent

    fun init(context: Context) {
        appComponent = DaggerAppComponent
                .builder()
                .context(context)
                .build()
    }

    override fun inject(fragment: Fragment) {
        when(fragment) {
            is TripFragment -> injectTripFragment(fragment)
            is HistoryFragment -> injectHistoryFragment(fragment)
            is TripsListFragment -> injectTripsListFragment(fragment)
            is LaunchFragment -> injectLaunchFragment(fragment)
        }
    }

    private fun injectTripFragment(tripFragment: TripFragment) {
        DaggerTripComponent
                .builder()
                .appComponent(appComponent)
                .fragment(tripFragment)
                .build()
                .inject(tripFragment)
    }

    private fun injectHistoryFragment(historyFragment: HistoryFragment) {
        DaggerHistoryComponent
                .builder()
                .appComponent(appComponent)
                .fragment(historyFragment)
                .build()
                .inject(historyFragment)
    }

    private fun injectTripsListFragment(tripsListFragment: TripsListFragment) {
        DaggerTripListComponent
                .builder()
                .appComponent(appComponent)
                .fragment(tripsListFragment)
                .build()
                .inject(tripsListFragment)
    }

    private fun injectLaunchFragment(launchFragment: LaunchFragment) {
        DaggerLaunchComponent
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(launchFragment)
    }
}