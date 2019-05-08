package com.antonchaynikov.triptracker.mainscreen

import android.location.Location
import androidx.test.platform.app.InstrumentationRegistry
import com.antonchaynikov.triptracker.MockLocationSource
import com.antonchaynikov.core.utils.data.location.LocationSource
import com.antonchaynikov.core.utils.data.location.LocationSourceModule
import com.antonchaynikov.core.utils.data.repository.Repository
import com.antonchaynikov.triptracker.injection.AppComponent
import com.antonchaynikov.triptracker.injection.AppModule
import com.antonchaynikov.triptracker.injection.Injector
import it.cosenonjaviste.daggermock.DaggerMockRule

class TripFragmentMockInjectionRule(locations: List<Location>): DaggerMockRule<TripComponent>(
        TripComponent::class.java,
        TripModule(null, true),
        com.antonchaynikov.core.utils.data.location.LocationSourceModule()) {

    val injectedRepository: com.antonchaynikov.core.utils.data.repository.Repository
    val injectedLocationSource: MockLocationSource

    init {
        injectedRepository = MockRepository()
        injectedLocationSource = MockLocationSource(locations)
        addComponentDependency(AppComponent::class.java, AppModule(InstrumentationRegistry.getInstrumentation().targetContext))
        provides(com.antonchaynikov.core.utils.data.repository.Repository::class.java, injectedRepository)
        provides(com.antonchaynikov.core.utils.data.location.LocationSource::class.java, injectedLocationSource)
        set {
            Injector.isTestMode = true
            Injector.tripComponent = it
        }
    }
}