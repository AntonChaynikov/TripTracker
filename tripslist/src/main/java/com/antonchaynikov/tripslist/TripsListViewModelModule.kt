package com.antonchaynikov.tripslist

import com.antonchaynikov.core.authentication.Auth
import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.core.viewmodel.BasicViewModel
import com.antonchaynikov.core.viewmodel.StatisticsFormatter
import com.antonchaynikov.core.viewmodel.ViewModelFactory
import com.antonchaynikov.core.viewmodel.ViewModelProviders
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object TripsListViewModelModule {
    @TripsListScope
    @JvmStatic
    @Provides
    fun viewModel(fragment: TripsListFragment, @Named("TripsListViewModel") factory: ViewModelFactory): TripsListViewModel =
            ViewModelProviders.of(fragment, factory).get(TripsListViewModel::class.java)

    @JvmStatic
    @TripsListScope
    @Provides
    @Named("TripsListViewModel")
    fun factory(repository: Repository, auth: Auth, statsFormatter: StatisticsFormatter): ViewModelFactory = object: ViewModelFactory {
        override fun <T : BasicViewModel?> create(clazz: Class<T>): T = TripsListViewModel(repository, auth, statsFormatter) as T
    }
}