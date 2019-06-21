package com.antonchaynikov.triptracker

import com.antonchaynikov.login.LaunchFragment
import com.antonchaynikov.login.LoginScope
import com.antonchaynikov.login.NavigationLogin
import com.antonchaynikov.triptracker.application.AppComponent
import dagger.Component
import dagger.Subcomponent

@LoginScope
@Subcomponent
interface LaunchComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): LaunchComponent
    }

    fun inject(launchFragment: LaunchFragment)
}