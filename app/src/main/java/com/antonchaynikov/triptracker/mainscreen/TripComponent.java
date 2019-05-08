package com.antonchaynikov.triptracker.mainscreen;

import com.antonchaynikov.core.data.tripmanager.TripManagerModule;
import com.antonchaynikov.core.viewmodel.CommonViewModelModule;
import com.antonchaynikov.core.authentication.AuthModule;
import com.antonchaynikov.triptracker.injection.AppComponent;

import dagger.Component;

@TripScope
@Component(
        dependencies = AppComponent.class,
        modules = {TripModule.class, CommonViewModelModule.class, AuthModule.class, TripManagerModule.class})
public interface TripComponent {
    void inject(TripFragment fragment);
}
