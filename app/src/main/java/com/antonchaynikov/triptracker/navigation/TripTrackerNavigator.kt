package com.antonchaynikov.triptracker.navigation

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.antonchaynikov.login.LaunchFragment
import com.antonchaynikov.login.NavigationLogin
import com.antonchaynikov.tripscreen.NavigationTripScreen
import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.triptracker.R

class TripTrackerNavigator(): NavigationLogin, NavigationTripScreen {

    override fun onLoggedIn(fragment: LaunchFragment) {
        NavHostFragment.findNavController(fragment).navigate(R.id.action_launchFragment_to_tripFragment)
    }

    override fun navigateToStatistics(currentFragment: TripFragment) {
        NavHostFragment.findNavController(currentFragment).navigate(R.id.action_tripFragment_to_tripsListFragment)
    }

    override fun navigateToSummary(currentFragment: TripFragment, tripStartDate: Long) {
        val args = Bundle(1)
        val key = currentFragment.getString(R.string.tripStartDate)
        args.putLong(key, tripStartDate)
        NavHostFragment.findNavController(currentFragment).navigate(R.id.action_tripFragment_to_historyFragment, args)
    }

    override fun logout(currentFragment: TripFragment) {
        NavHostFragment.findNavController(currentFragment).navigate(R.id.action_tripFragment_to_launchFragment)
    }
}