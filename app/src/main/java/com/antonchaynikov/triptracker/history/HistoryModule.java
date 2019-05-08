package com.antonchaynikov.triptracker.history;

import androidx.annotation.NonNull;

import com.antonchaynikov.core.data.repository.Repository;
import com.antonchaynikov.core.viewmodel.BasicViewModel;
import com.antonchaynikov.core.viewmodel.StatisticsFormatter;
import com.antonchaynikov.core.viewmodel.ViewModelFactory;
import com.antonchaynikov.core.viewmodel.ViewModelProviders;

import dagger.Module;
import dagger.Provides;

@Module
public class HistoryModule {

    private HistoryFragment mFragment;
    private long mTripStartDate;

    public HistoryModule(HistoryFragment fragment, long tripStartDate) {
        mFragment = fragment;
        mTripStartDate = tripStartDate;
    }

    @Provides
    public HistoryViewModel provideViewModel(ViewModelFactory factory) {
        return ViewModelProviders.of(mFragment, factory).get(HistoryViewModel.class);
    }

    @Provides
    public ViewModelFactory provideViewModelFactory(Repository repository, StatisticsFormatter statisticsFormatter) {
        return new ViewModelFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends BasicViewModel> T create(@NonNull Class<T> clazz) {
                return (T) new HistoryViewModel(
                        repository,
                        statisticsFormatter,
                        mTripStartDate);
            }
        };
    }
}
