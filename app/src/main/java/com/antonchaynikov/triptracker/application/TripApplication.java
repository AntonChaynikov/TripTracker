package com.antonchaynikov.triptracker.application;

import com.antonchaynikov.triptracker.authentication.AuthModule;
import com.antonchaynikov.triptracker.data.location.LocationSourceModule;
import com.antonchaynikov.triptracker.data.tripmanager.TripManagerModule;
import com.antonchaynikov.triptracker.history.DaggerHistoryComponent;
import com.antonchaynikov.triptracker.history.HistoryActivity;
import com.antonchaynikov.triptracker.history.HistoryModule;

import com.antonchaynikov.triptracker.mainscreen.DaggerTripComponent;
import com.antonchaynikov.triptracker.mainscreen.TripActivity;

import com.antonchaynikov.triptracker.mainscreen.TripModule;
import com.antonchaynikov.triptracker.trips.DaggerTripsListComponent;
import com.antonchaynikov.triptracker.trips.TripsListActivity;
import com.antonchaynikov.triptracker.trips.TripsListModule;
import com.antonchaynikov.triptracker.viewmodel.CommonViewModelModule;

import androidx.multidex.MultiDexApplication;

public class TripApplication extends MultiDexApplication {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();
    }

    public void injectTripsListActivityDependencies(TripsListActivity activity) {
       DaggerTripsListComponent.builder()
                .appComponent(mAppComponent)
                .tripsListModule(new TripsListModule(activity))
                .build()
                .inject(activity);
    }

    public void injectHistoryActivityDependencies(HistoryActivity activity, long tripStartDate) {
       DaggerHistoryComponent.builder()
                .appComponent(mAppComponent)
                .historyModule(new HistoryModule(activity, tripStartDate))
                .commonViewModelModule(new CommonViewModelModule())
                .build()
                .inject(activity);
    }

    public void injectTripActivityDependencies(TripActivity activity, boolean isLocationPermissionGranted) {
        DaggerTripComponent.builder()
                .appComponent(mAppComponent)
                .authModule(new AuthModule())
                .commonViewModelModule(new CommonViewModelModule())
                .locationSourceModule(new LocationSourceModule())
                .tripManagerModule(new TripManagerModule())
                .tripModule(new TripModule(activity, isLocationPermissionGranted))
                .build()
                .inject(activity);
    }
}
