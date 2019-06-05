package com.antonchaynikov.triptracker

import com.antonchaynikov.login.LaunchFragment
import com.antonchaynikov.login.LoginScope
import com.antonchaynikov.triptracker.application.AppComponent
import dagger.Component

@LoginScope
@Component(dependencies = [AppComponent::class])
interface LaunchComponent {
    fun inject(launchFragment: LaunchFragment)
}