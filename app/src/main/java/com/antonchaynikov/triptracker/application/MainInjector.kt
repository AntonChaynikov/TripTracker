package com.antonchaynikov.triptracker.application

import android.content.Context
import androidx.fragment.app.Fragment
import com.antonchaynikov.core.injection.IInjector
import com.antonchaynikov.login.LaunchFragment
import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.tripshistory.HistoryFragment
import com.antonchaynikov.tripslist.TripsListFragment

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

    }

    private fun injectHistoryFragment(historyFragment: HistoryFragment) {

    }

    private fun injectTripsListFragment(tripsListFragment: TripsListFragment) {

    }

    private fun injectLaunchFragment(launchFragment: LaunchFragment) {

    }
}