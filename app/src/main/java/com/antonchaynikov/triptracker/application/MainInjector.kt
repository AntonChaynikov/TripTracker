package com.antonchaynikov.triptracker.application

import android.content.Context
import androidx.annotation.VisibleForTesting
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

    @VisibleForTesting
    fun setTestAppComponent(testAppComponent: AppComponent) {
        appComponent = testAppComponent
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
        appComponent
                .tripComponent()
                .fragment(tripFragment)
                .build()
                .inject(tripFragment)
    }

    private fun injectHistoryFragment(historyFragment: HistoryFragment) {
        appComponent
                .historyComponent()
                .fragment(historyFragment)
                .build()
                .inject(historyFragment)
    }

    private fun injectTripsListFragment(tripsListFragment: TripsListFragment) {
        appComponent
                .tripListComponent()
                .fragment(tripsListFragment)
                .build()
                .inject(tripsListFragment)
    }

    private fun injectLaunchFragment(launchFragment: LaunchFragment) {
        appComponent
                .launchComponent()
                .build()
                .inject(launchFragment)
    }
}