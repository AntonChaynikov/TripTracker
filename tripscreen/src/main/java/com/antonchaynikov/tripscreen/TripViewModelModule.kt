package com.antonchaynikov.tripscreen

import dagger.Module
import dagger.Provides
import com.antonchaynikov.core.viewmodel.BasicViewModel
import com.antonchaynikov.core.viewmodel.ViewModelFactory
import com.antonchaynikov.core.viewmodel.StatisticsFormatter
import com.google.firebase.auth.FirebaseAuth
import com.antonchaynikov.core.data.tripmanager.TripManager
import com.antonchaynikov.core.viewmodel.ViewModelProviders

@Module
object TripViewModelModule {

    @JvmStatic
    @Provides
    fun viewModel(fragment: TripFragment, factory: ViewModelFactory): TripViewModel =
            ViewModelProviders.of(fragment, factory).get(TripViewModel::class.java)

    @JvmStatic
    @Provides
    fun provideTripViewModelFactory(fragment: TripFragment,
                                    tripManager: TripManager,
                                    firebaseAuth: FirebaseAuth,
                                    statisticsFormatter: StatisticsFormatter): ViewModelFactory {
        return object : ViewModelFactory {
            override fun <T : BasicViewModel> create(clazz: Class<T>): T {
                return TripViewModel(
                        tripManager,
                        firebaseAuth,
                        statisticsFormatter,
                        fragment.isLocationPermissionGranted) as T
            }
        }
    }

}