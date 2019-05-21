package com.antonchaynikov.tripslist

import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.core.viewmodel.BasicViewModel
import com.antonchaynikov.core.viewmodel.ViewModelFactory
import com.antonchaynikov.core.viewmodel.ViewModelProviders
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object TripsListViewModelModule {
    @JvmStatic
    @Provides
    fun viewModel(fragment: TripsListFragment, @Named("TripsListViewModel") factory: ViewModelFactory): TripsListViewModel =
            ViewModelProviders.of(fragment, factory).get(TripsListViewModel::class.java)

    @JvmStatic
    @Provides
    @Named("TripsListViewModel")
    fun factory(repository: Repository): ViewModelFactory = object: ViewModelFactory {
        override fun <T : BasicViewModel?> create(clazz: Class<T>): T = TripsListViewModel(repository) as T
    }
}