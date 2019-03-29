package com.antonchaynikov.triptracker.trips;

import com.antonchaynikov.triptracker.application.AppComponent;

import javax.inject.Singleton;

import dagger.Component;

@TripsListScope
@Component(dependencies = AppComponent.class, modules = {TripsListModule.class})
public interface TripsListComponent {
    void inject(TripsListFragment fragment);
}
