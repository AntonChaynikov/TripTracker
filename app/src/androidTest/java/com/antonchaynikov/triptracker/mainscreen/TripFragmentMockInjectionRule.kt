package com.antonchaynikov.triptracker.mainscreen

import android.location.Location
import androidx.test.platform.app.InstrumentationRegistry
import com.antonchaynikov.triptracker.MockLocationSource
import com.antonchaynikov.triptracker.data.location.LocationSource
import com.antonchaynikov.triptracker.data.location.LocationSourceModule
import com.antonchaynikov.triptracker.data.repository.Repository
import com.antonchaynikov.triptracker.injection.AppComponent
import com.antonchaynikov.triptracker.injection.AppModule
import com.antonchaynikov.triptracker.injection.Injector
import it.cosenonjaviste.daggermock.DaggerMockRule

class TripFragmentMockInjectionRule(locations: List<Location>): DaggerMockRule<TripComponent>(
        TripComponent::class.java,
        TripModule(null, true),
        LocationSourceModule()) {

    val injectedRepository: Repository
    val injectedLocationSource: MockLocationSource

    init {
        injectedRepository = MockRepository()
        injectedLocationSource = MockLocationSource(locations)
        addComponentDependency(AppComponent::class.java, AppModule(InstrumentationRegistry.getInstrumentation().targetContext))
        provides(Repository::class.java, injectedRepository)
        provides(LocationSource::class.java, injectedLocationSource)
        set {
            Injector.isTestMode = true
            Injector.tripComponent = it
        }
    }
}