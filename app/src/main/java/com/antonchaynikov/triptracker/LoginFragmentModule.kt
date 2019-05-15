package com.antonchaynikov.triptracker

import com.antonchaynikov.login.LaunchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface LoginFragmentModule {
    @ContributesAndroidInjector
    fun loginFragment(): LaunchFragment
}