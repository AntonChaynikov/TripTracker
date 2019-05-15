package com.antonchaynikov.tripscreen

interface NavigationTripScreen {
    fun navigateToStatistics(currentFragment: TripFragment)
    fun navigateToSummary(currentFragment: TripFragment, tripStartDate: Long)
    fun logout(currentFragment: TripFragment)
}