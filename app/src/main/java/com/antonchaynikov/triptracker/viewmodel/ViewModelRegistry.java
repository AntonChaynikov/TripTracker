package com.antonchaynikov.triptracker.viewmodel;

import java.util.HashMap;

import androidx.annotation.NonNull;

class ViewModelRegistry {

    private HashMap<String, BasicViewModel> mViewModels = new HashMap<>();

    public BasicViewModel get(@NonNull String key) {
        return mViewModels.get(key);
    }

    public void put(@NonNull String key, @NonNull BasicViewModel viewModel) {
        BasicViewModel prevViewModel = mViewModels.put(key, viewModel);
        if (prevViewModel != null) {
            prevViewModel.onCleared();
        }
    }

    public void clear() {
        for (BasicViewModel vm: mViewModels.values()) {
            vm.onCleared();
        }
    }

}
