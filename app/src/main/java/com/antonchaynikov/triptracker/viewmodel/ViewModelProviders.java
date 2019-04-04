package com.antonchaynikov.triptracker.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class ViewModelProviders {

    private static ViewModelFactory sDefaultFactory;

    private ViewModelProviders() {}

    public static ViewModelProvider of(@NonNull ViewModelActivity activity) {
        return new ViewModelProvider(activity.getViewModelRegistry(), null);
    }

    public static ViewModelProvider of(@NonNull ViewModelActivity activity, @Nullable ViewModelFactory factory) {
        return new ViewModelProvider(activity.getViewModelRegistry(), factory == null ? getDefaultFactory() : factory);
    }

    public static ViewModelProvider of(@NonNull ViewModelFragment fragment) {
        return new ViewModelProvider(fragment.getViewModelRegistry(), null);
    }

    public static ViewModelProvider of(@NonNull ViewModelFragment fragment, @Nullable ViewModelFactory factory) {
        return new ViewModelProvider(fragment.getViewModelRegistry(), factory == null ? getDefaultFactory() : factory);
    }

    private static ViewModelFactory getDefaultFactory() {
        if (sDefaultFactory == null) {
            sDefaultFactory = new ViewModelFactory() {
                @Override
                public <T extends BasicViewModel> T  create(@NonNull Class<T> clazz) {
                    T viewModelInstance = null;
                    try {
                        viewModelInstance = clazz.newInstance();
                    } catch (IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                    return viewModelInstance;
                }
            };
        }
        return sDefaultFactory;
    }

}
