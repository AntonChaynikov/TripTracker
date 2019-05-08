package com.antonchaynikov.triptracker.mainscreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.antonchaynikov.core.data.tripmanager.TripManager;
import com.antonchaynikov.core.viewmodel.BasicViewModel;
import com.antonchaynikov.core.viewmodel.StatisticsFormatter;
import com.antonchaynikov.core.viewmodel.ViewModelFactory;
import com.antonchaynikov.core.viewmodel.ViewModelProviders;
import com.google.firebase.auth.FirebaseAuth;

import dagger.Module;
import dagger.Provides;

@Module
public class TripModule {

    private TripFragment mFragment;
    private boolean mIsLocationPermissionGranted;

    public TripModule(@Nullable TripFragment fragment, boolean isLocationPermissionGranted) {
        mFragment = fragment;
        mIsLocationPermissionGranted = isLocationPermissionGranted;
    }

    @Provides
    public TripViewModel provideTripViewModel(ViewModelFactory factory) {
        return mFragment == null ?
                factory.create(TripViewModel.class) :
                ViewModelProviders.of(mFragment, factory).get(TripViewModel.class);
    }

    @Provides
    public ViewModelFactory provideTripViewModelFactory(TripManager tripManager,
                                                        FirebaseAuth firebaseAuth,
                                                        StatisticsFormatter statisticsFormatter) {
        return new ViewModelFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends BasicViewModel> T create(@NonNull Class<T> clazz) {
                return (T) new TripViewModel(
                        tripManager,
                        firebaseAuth,
                        statisticsFormatter,
                        mIsLocationPermissionGranted);
            }
        };
    }
}
