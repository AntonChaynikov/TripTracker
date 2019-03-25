package com.antonchaynikov.triptracker.mainscreen;

import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter;
import com.antonchaynikov.triptracker.viewmodel.ViewModelFactory;
import com.antonchaynikov.triptracker.viewmodel.ViewModelProviders;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;

@Module
public class TripModule {

    private TripActivity mActivity;
    private boolean mIsLocationPermissionGranted;

    public TripModule(TripActivity activity, boolean isLocationPermissionGranted) {
        mActivity = activity;
        mIsLocationPermissionGranted = isLocationPermissionGranted;
    }

    @Provides
    public TripViewModel provideTripViewModel(ViewModelFactory factory) {
        return ViewModelProviders.of(mActivity, factory).get(TripViewModel.class);
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
