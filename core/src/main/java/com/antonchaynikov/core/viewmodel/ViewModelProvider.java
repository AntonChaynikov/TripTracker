package com.antonchaynikov.core.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ViewModelProvider {

    private ViewModelRegistry mViewModelRegistry;
    private ViewModelFactory mFactory;

    public ViewModelProvider(@Nullable ViewModelRegistry registry, @Nullable ViewModelFactory factory) {
        mViewModelRegistry = registry == null ? new ViewModelRegistry() : registry;
        mFactory = factory;
    }

    @SuppressWarnings("unchecked")
    public <T extends BasicViewModel> T get(@NonNull Class<T> viewModelClass) {
        String canonicalName = viewModelClass.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Anonymous classes cannot be viewmodels");
        }
        BasicViewModel vm = mViewModelRegistry.get(canonicalName);
        if (vm == null) {
            vm = mFactory.create(viewModelClass);
            mViewModelRegistry.put(viewModelClass.getCanonicalName(), vm);
        }
        return (T) vm;
    }
}
