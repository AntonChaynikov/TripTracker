package com.antonchaynikov.triptracker

import com.antonchaynikov.login.NavigationLogin
import com.antonchaynikov.tripscreen.NavigationTripScreen
import com.antonchaynikov.triptracker.navigation.TripTrackerNavigator
import dagger.Module
import dagger.Provides

@Module
object NavigationModule {
    @JvmStatic
    @Provides
    fun navigationLogin(tripTrackerNavigator: TripTrackerNavigator): NavigationLogin = tripTrackerNavigator

    @JvmStatic
    @Provides
    fun navigationTripScreen(tripTrackerNavigator: TripTrackerNavigator): NavigationTripScreen = tripTrackerNavigator

    @JvmStatic
    @Provides
    fun tripTrackerNavigator() = TripTrackerNavigator()
}