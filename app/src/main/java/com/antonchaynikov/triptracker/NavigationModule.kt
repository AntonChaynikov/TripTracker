package com.antonchaynikov.triptracker

import com.antonchaynikov.login.NavigationLogin
import com.antonchaynikov.tripscreen.NavigationTripScreen
import com.antonchaynikov.triptracker.navigation.TripTrackerNavigator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class NavigationModule {

    @Provides
    fun navigationLogin(tripTrackerNavigator: TripTrackerNavigator): NavigationLogin = tripTrackerNavigator

    @Singleton
    @Provides
    fun navigationTripScreen(tripTrackerNavigator: TripTrackerNavigator): NavigationTripScreen = tripTrackerNavigator

    @Singleton
    @Provides
    fun tripTrackerNavigator() = TripTrackerNavigator()
}