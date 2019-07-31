package com.antonchaynikov.triptracker.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.antonchaynikov.login.LaunchFragment
import com.antonchaynikov.login.NavigationLogin
import com.antonchaynikov.tripscreen.NavigationTripScreen
import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.tripslist.NavigationTripsList
import com.antonchaynikov.tripslist.TripsListFragment
import com.antonchaynikov.triptracker.R

class TripTrackerNavigator(): NavigationLogin, NavigationTripScreen, NavigationTripsList {

    override fun onLoggedIn(fragment: LaunchFragment) {
        NavHostFragment.findNavController(fragment).navigate(R.id.action_dest_auth_to_dest_trip)
    }

    override fun navigateToStatistics(currentFragment: TripFragment) {
        NavHostFragment.findNavController(currentFragment).navigate(R.id.action_dest_trip_to_dest_statistics)
        NavHostFragment.findNavController(currentFragment).popBackStack()
    }

    override fun navigateToSummary(currentFragment: TripFragment, tripStartDate: Long) {
        val args = createStartDateArgs(currentFragment, tripStartDate)
        NavHostFragment.findNavController(currentFragment).navigate(R.id.action_dest_trip_to_dest_history, args)
    }

    override fun logout(currentFragment: TripFragment) {
        NavHostFragment.findNavController(currentFragment).navigate(R.id.action_dest_trip_to_dest_auth)
    }

    override fun navigateOnLogoutTripsList(currentFragment: TripsListFragment) {
        NavHostFragment.findNavController(currentFragment).navigate(R.id.dest_trip)
    }

    override fun navigateOnItemClicked(currentFragment: TripsListFragment, tripStartDate: Long) {
        val args = createStartDateArgs(currentFragment, tripStartDate)
        NavHostFragment.findNavController(currentFragment).navigate(R.id.action_dest_statistics_to_dest_history, args)
    }

    private fun createStartDateArgs(fragment: Fragment, tripStartDate: Long): Bundle {
        val args = Bundle(1)
        val key = fragment.getString(R.string.tripStartDate)
        args.putLong(key, tripStartDate)
        return args
    }
}