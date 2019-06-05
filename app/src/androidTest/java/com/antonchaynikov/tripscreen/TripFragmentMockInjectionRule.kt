package com.antonchaynikov.tripscreen

import android.location.Location
import androidx.fragment.app.Fragment
import androidx.test.platform.app.InstrumentationRegistry
import com.antonchaynikov.core.RepositoryModule
import com.antonchaynikov.core.data.location.LocationSource
import com.antonchaynikov.core.data.location.LocationSourceModule
import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.core.injection.IInjector
import com.antonchaynikov.core.injection.Injector
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
        set {Injector.init(object: IInjector {
            override fun inject(fragment: Fragment) {

            }
        })}
    }

    fun getContext() = InstrumentationRegistry.getInstrumentation().targetContext

    fun getApplication() = getContext().applicationContext as TripApplication
}