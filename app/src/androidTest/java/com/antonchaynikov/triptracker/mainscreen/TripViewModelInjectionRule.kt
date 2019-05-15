package com.antonchaynikov.triptracker.mainscreen

import android.location.Location
import androidx.test.platform.app.InstrumentationRegistry
import com.antonchaynikov.triptracker.MockLocationSource
import com.antonchaynikov.triptracker.data.location.LocationSource
import com.antonchaynikov.triptracker.data.location.LocationSourceModule
import com.antonchaynikov.triptracker.injection.AppComponent
import com.antonchaynikov.triptracker.injection.AppModule
import it.cosenonjaviste.daggermock.DaggerMockRule

class TripViewModelInjectionRule(locations: List<Location>): DaggerMockRule<TripComponent>(
        TripComponent::class.java,
        TripModule(null, true),
        LocationSourceModule()) {

    val injectedLocationSource: MockLocationSource

    init {
        injectedLocationSource = MockLocationSource(locations)
        addComponentDependency(AppComponent::class.java, AppModule(InstrumentationRegistry.getInstrumentation().targetContext))
        provides(LocationSource::class.java, injectedLocationSource)
    }
}