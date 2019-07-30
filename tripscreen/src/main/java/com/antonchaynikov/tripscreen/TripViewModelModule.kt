package com.antonchaynikov.tripscreen

import com.antonchaynikov.core.authentication.Auth
import dagger.Module
import dagger.Provides
import com.antonchaynikov.core.viewmodel.BasicViewModel
import com.antonchaynikov.core.viewmodel.ViewModelFactory
import com.antonchaynikov.core.viewmodel.StatisticsFormatter
import com.google.firebase.auth.FirebaseAuth
import com.antonchaynikov.core.data.tripmanager.TripManager
import com.antonchaynikov.core.viewmodel.ViewModelProviders
import javax.inject.Named

@Module
object TripViewModelModule {

    @JvmStatic
    @TripsScreenScope
    @Provides
    fun viewModel(fragment: TripFragment, @Named("TripViewModel") factory: ViewModelFactory): TripViewModel =
            ViewModelProviders.of(fragment, factory).get(TripViewModel::class.java)

    @JvmStatic
    @Provides
    @TripsScreenScope
    @Named("TripViewModel")
    fun provideTripViewModelFactory(fragment: TripFragment,
                                    tripManager: TripManager,
                                    auth: Auth,
                                    statisticsFormatter: StatisticsFormatter): ViewModelFactory {
        return object : ViewModelFactory {
            override fun <T : BasicViewModel> create(clazz: Class<T>): T {
                return TripViewModel(
                        tripManager,
                        auth,
                        statisticsFormatter,
                        fragment.isLocationPermissionGranted) as T
            }
        }
    }

}