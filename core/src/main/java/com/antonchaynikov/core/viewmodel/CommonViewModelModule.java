package com.antonchaynikov.core.viewmodel;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CommonViewModelModule {

    @Singleton
    @Provides
    public StatisticsFormatter provideStatisticsFormatter(Context context) {
        return new StatisticsFormatter(context);
    }
}
