package com.antonchaynikov.triptracker.application

import com.antonchaynikov.triptracker.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ContainerActivityModule {
    @ContributesAndroidInjector(modules = [TestFragmentsBindingModule::class])
    internal abstract fun containerActivity(): ContainerActivity
}