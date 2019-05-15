package com.antonchaynikov.triptracker

import com.antonchaynikov.tripscreen.TripFragment
import com.antonchaynikov.tripscreen.TripViewModelModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface TripFragmentModule {
    @ContributesAndroidInjector(modules = [TripViewModelModule::class])
    fun tripFragment(): TripFragment
}