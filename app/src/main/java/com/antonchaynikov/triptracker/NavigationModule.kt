package com.antonchaynikov.triptracker

import com.antonchaynikov.login.NavigationLogin
import com.antonchaynikov.tripscreen.NavigationTripScreen
import com.antonchaynikov.triptracker.navigation.TripTrackerNavigator
import dagger.Module
import dagger.Provides

@Module
open class NavigationModule {

    @Provides
    fun navigationLogin(tripTrackerNavigator: TripTrackerNavigator): NavigationLogin = tripTrackerNavigator

    @Provides
    fun navigationTripScreen(tripTrackerNavigator: TripTrackerNavigator): NavigationTripScreen = tripTrackerNavigator

    @Provides
    fun tripTrackerNavigator() = TripTrackerNavigator()
}