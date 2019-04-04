package com.antonchaynikov.triptracker.trips;

import com.antonchaynikov.triptracker.data.repository.Repository;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;
import com.antonchaynikov.triptracker.viewmodel.ViewModelFactory;
import com.antonchaynikov.triptracker.viewmodel.ViewModelFragment;
import com.antonchaynikov.triptracker.viewmodel.ViewModelProviders;

import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;

@Module
public class TripsListModule {

    private ViewModelFragment mFragment;

    public TripsListModule(ViewModelFragment fragment) {
        mFragment = fragment;
    }

    @Provides
    TripsListViewModel provideTripsListViewModel(ViewModelFactory factory) {
        return ViewModelProviders.of(mFragment, factory).get(TripsListViewModel.class);
    }

    @Provides
    ViewModelFactory provideFactory(Repository repository) {
        return new ViewModelFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends BasicViewModel> T create(@NonNull Class<T> clazz) {
                return (T) new TripsListViewModel(repository);
            }
        };
    }
}
