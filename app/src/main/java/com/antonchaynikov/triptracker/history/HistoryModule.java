package com.antonchaynikov.triptracker.history;

import com.antonchaynikov.triptracker.data.repository.Repository;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter;
import com.antonchaynikov.triptracker.viewmodel.ViewModelFactory;
import com.antonchaynikov.triptracker.viewmodel.ViewModelProviders;

import androidx.annotation.NonNull;
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
