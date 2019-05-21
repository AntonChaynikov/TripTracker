package com.antonchaynikov.tripscreen

import android.location.Location
import androidx.test.platform.app.InstrumentationRegistry
import com.antonchaynikov.core.data.location.LocationSource
import com.antonchaynikov.core.data.location.LocationSourceModule
import com.antonchaynikov.triptracker.application.TripApplication
import it.cosenonjaviste.daggermock.DaggerMockRule
import it.cosenonjaviste.daggermock.DaggerMockRule.BuilderCustomizer

class TripViewModelInjectionRule(locations: List<Location>): DaggerMockRule<AppComponent>(
        AppComponent::class.java, LocationSourceModule()) {

    val injectedLocationSource: MockLocationSource

    init {
        injectedLocationSource = MockLocationSource(locations)
        provides(LocationSource::class.java, injectedLocationSource)
        customizeBuilder(BuilderCustomizer<AppComponent.Builder> { builder -> builder.context(getApplication().applicationContext) })
        set { component -> component.inject(getApplication()) }
    }

    internal fun getApplication(): TripApplication = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TripApplication
}