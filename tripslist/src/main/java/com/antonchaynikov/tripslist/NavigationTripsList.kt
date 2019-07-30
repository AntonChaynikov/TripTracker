package com.antonchaynikov.tripslist

interface NavigationTripsList {
    fun navigateOnLogoutTripsList(currentFragment: TripsListFragment)
    fun navigateOnItemClicked(currentFragment: TripsListFragment, tripStartDate: Long)
}