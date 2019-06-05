package com.antonchaynikov.tripshistory

import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.core.viewmodel.BasicViewModel
import com.antonchaynikov.core.viewmodel.StatisticsFormatter
import com.antonchaynikov.core.viewmodel.ViewModelFactory
import com.antonchaynikov.core.viewmodel.ViewModelProviders
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object TripHistoryModule {

    @HistoryScope
    @JvmStatic
    @Provides
    fun viewModel(@Named("HistoryViewModel") factory: ViewModelFactory, fragment: HistoryFragment): HistoryViewModel =
            ViewModelProviders.of(fragment, factory).get(HistoryViewModel::class.java)

    @HistoryScope
    @JvmStatic
    @Provides
    @Named("HistoryViewModel")
    fun factory(repository: Repository,
                statisticsFormatter: StatisticsFormatter,
                fragment: HistoryFragment): ViewModelFactory =
            object : ViewModelFactory {
                override fun <T : BasicViewModel> create(clazz: Class<T>): T = HistoryViewModel(
                        repository,
                        statisticsFormatter,
                        fragment.tripStartDate) as T
            }
}