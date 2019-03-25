package com.antonchaynikov.triptracker.data.tripmanager;

import com.antonchaynikov.triptracker.data.location.LocationSource;
import com.antonchaynikov.triptracker.data.location.LocationSourceModule;
import com.antonchaynikov.triptracker.data.repository.Repository;

import dagger.Module;
import dagger.Provides;

@Module(includes = LocationSourceModule.class)
public class TripManagerModule {

    @Provides
    public TripManager provideTripManager(Repository repository, LocationSource locationSource, StatisticsCalculator statisticsCalculator) {
        return TripManager.getInstance(repository, locationSource, statisticsCalculator);
    }

    @Provides
    public StatisticsCalculator provideStatisticsCalculator() {
        return new StatisticsCalculator();
    }

}
