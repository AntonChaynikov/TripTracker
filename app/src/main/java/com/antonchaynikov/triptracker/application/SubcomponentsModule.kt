package com.antonchaynikov.triptracker.application

import com.antonchaynikov.triptracker.HistoryComponent
import com.antonchaynikov.triptracker.LaunchComponent
import com.antonchaynikov.triptracker.TripComponent
import com.antonchaynikov.triptracker.TripListComponent
import dagger.Module

@Module(subcomponents = [TripComponent::class, HistoryComponent::class, TripListComponent::class, LaunchComponent::class])
class SubcomponentsModule {
}