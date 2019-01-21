package com.antonchaynikov.triptracker.viewmodel;

import androidx.annotation.NonNull;

public class ViewModelProvider {

    private ViewModelRegistry mViewModelRegistry;
    private ViewModelFactory mFactory;

    public ViewModelProvider(@NonNull ViewModelRegistry registry, @NonNull ViewModelFactory factory) {
        mViewModelRegistry = registry;
        mFactory = factory;
    }

    public BasicViewModel get(@NonNull Class<? extends BasicViewModel> viewModelClass) {
        BasicViewModel vm = mViewModelRegistry.get(viewModelClass.getCanonicalName());
        if (vm == null) {
            vm = mFactory.create(viewModelClass);
            mViewModelRegistry.put(viewModelClass.getCanonicalName(), vm);
        }
        return vm;
    }

}
