package com.antonchaynikov.triptracker.history;

import com.antonchaynikov.triptracker.application.AppComponent;
import com.antonchaynikov.triptracker.viewmodel.CommonViewModelModule;
import com.antonchaynikov.triptracker.viewmodel.CommonViewModelScope;

import dagger.Component;

@CommonViewModelScope
@Component(dependencies = AppComponent.class, modules = {HistoryModule.class, CommonViewModelModule.class})
public interface HistoryComponent {
    void inject(HistoryActivity activity);
}
