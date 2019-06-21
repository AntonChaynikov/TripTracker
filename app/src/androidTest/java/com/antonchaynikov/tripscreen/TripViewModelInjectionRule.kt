package com.antonchaynikov.tripscreen

import android.content.Context
import android.location.Location
import androidx.test.platform.app.InstrumentationRegistry
import com.antonchaynikov.core.data.location.LocationSource
import com.antonchaynikov.core.data.location.LocationSourceModule
import com.antonchaynikov.triptracker.application.AppComponent
import com.antonchaynikov.triptracker.application.MainInjector
import it.cosenonjaviste.daggermock.DaggerMockRule
import it.cosenonjaviste.daggermock.DaggerMockRule.BuilderCustomizer

class TripViewModelInjectionRule(locations: List<Location>): DaggerMockRule<AppComponent>(
        AppComponent::class.java, LocationSourceModule()) {

    val injectedLocationSource: MockLocationSource = MockLocationSource(locations)

    init {
        provides(LocationSource::class.java, injectedLocationSource)
        customizeBuilder(BuilderCustomizer<AppComponent.Builder> {
            it.context(getContext())
        })
        set {MainInjector.setTestAppComponent(it)}
    }

    internal fun getContext(): Context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
}