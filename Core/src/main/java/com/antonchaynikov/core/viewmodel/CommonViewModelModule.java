package com.antonchaynikov.core.viewmodel;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class CommonViewModelModule {

    @Provides
    public StatisticsFormatter provideStatisticsFormatter(Context context) {
        return new StatisticsFormatter(context);
    }
}
