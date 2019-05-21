package com.antonchaynikov.tripscreen

import android.location.Location
import androidx.test.platform.app.InstrumentationRegistry
import com.antonchaynikov.core.RepositoryModule
import com.antonchaynikov.core.TripManagerModule
import com.antonchaynikov.core.data.location.LocationSource
import com.antonchaynikov.core.data.location.LocationSourceModule
import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.core.injection.Injector
import com.antonchaynikov.triptracker.application.TestComponent
import it.cosenonjaviste.daggermock.DaggerMockRule

class TripFragmentMockInjectionRule(locations: List<Location>): DaggerMockRule<TestComponent>(
        TestComponent::class.java, RepositoryModule(), LocationSourceModule()) {

    val injectedRepository: Repository
    val injectedLocationSource: MockLocationSource

    init {
        injectedRepository = MockRepository()
        injectedLocationSource = MockLocationSource(locations)
        customizeBuilder<TestComponent.Builder> {
            builder -> builder
                .context(getContext())
                .tripManagerModule(TripManagerModule())
                .repositoryModule(RepositoryModule())
                .locationSourceModule(LocationSourceModule())
        }
        provides(Repository::class.java, injectedRepository)
        provides(LocationSource::class.java, injectedLocationSource)
        set {Injector.init(it)}
    }

    fun getContext() = InstrumentationRegistry.getInstrumentation().targetContext
}