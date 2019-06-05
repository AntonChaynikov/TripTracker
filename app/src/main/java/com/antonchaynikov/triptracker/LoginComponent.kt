package com.antonchaynikov.triptracker

import com.antonchaynikov.login.LaunchFragment
import com.antonchaynikov.triptracker.application.AppComponent
import dagger.Component

@Component(dependencies = [AppComponent::class])
interface LoginComponent {
    fun inject(launchFragment: LaunchFragment)
}