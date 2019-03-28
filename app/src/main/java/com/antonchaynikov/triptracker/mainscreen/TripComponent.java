package com.antonchaynikov.triptracker.mainscreen;

import com.antonchaynikov.triptracker.application.AppComponent;
import com.antonchaynikov.triptracker.authentication.AuthModule;
import com.antonchaynikov.triptracker.data.tripmanager.TripManagerModule;
import com.antonchaynikov.triptracker.viewmodel.CommonViewModelModule;

import dagger.Component;

@TripScope
@Component(dependencies = AppComponent.class, modules = {TripModule.class, CommonViewModelModule.class, AuthModule.class, TripManagerModule.class})
public interface TripComponent {
    void inject(TripFragment fragment);
}
