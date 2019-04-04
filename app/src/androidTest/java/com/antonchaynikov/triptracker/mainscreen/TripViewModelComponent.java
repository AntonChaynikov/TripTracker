package com.antonchaynikov.triptracker.mainscreen;

import dagger.Component;

@Component(modules = {MockTripViewModelModule.class})
public interface TripViewModelComponent {
    void inject(TripFragmentTest destination);
}
