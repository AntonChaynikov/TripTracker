package com.antonchaynikov.tripscreen

import android.location.Location
import androidx.test.platform.app.InstrumentationRegistry
import com.antonchaynikov.core.RepositoryModule
import com.antonchaynikov.core.data.location.LocationSource
import com.antonchaynikov.core.data.location.LocationSourceModule
import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.triptracker.application.AppComponent
import com.antonchaynikov.triptracker.application.TripApplication

import it.cosenonjaviste.daggermock.DaggerMockRule

class TripFragmentMockInjectionRule(locations: List<Location>): DaggerMockRule<AppComponent>(
        AppComponent::class.java, RepositoryModule(), LocationSourceModule()) {

    val injectedRepository: Repository
    val injectedLocationSource: MockLocationSource

    init {
        injectedRepository = MockRepository()
        injectedLocationSource = MockLocationSource(locations)
        provides(Repository::class.java, injectedRepository)
        provides(LocationSource::class.java, injectedLocationSource)
        customizeBuilder(BuilderCustomizer<AppComponent.Builder> { builder -> builder.context(getApplication().applicationContext) })
        set { component -> component.inject(getApplication()) }
    }

    internal fun getApplication(): TripApplication = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TripApplication
}