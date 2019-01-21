package com.antonchaynikov.triptracker.viewmodel;

import androidx.annotation.NonNull;

public interface ViewModelFactory {
    <T extends BasicViewModel> T create(@NonNull Class<T> clazz);
}
