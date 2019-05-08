package com.antonchaynikov.triptracker.history;

import com.antonchaynikov.core.viewmodel.CommonViewModelModule;
import com.antonchaynikov.core.viewmodel.CommonViewModelScope;
import com.antonchaynikov.triptracker.injection.AppComponent;

import dagger.Component;

@CommonViewModelScope
@Component(dependencies = AppComponent.class, modules = {HistoryModule.class, CommonViewModelModule.class})
public interface HistoryComponent {
    void inject(HistoryFragment fragment);
}
