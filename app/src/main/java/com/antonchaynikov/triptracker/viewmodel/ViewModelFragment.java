package com.antonchaynikov.triptracker.viewmodel;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ViewModelFragment extends Fragment {
    private ViewModelRegistry mViewModelRegistry;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModelRegistry = new ViewModelRegistry();
    }

    ViewModelRegistry getViewModelRegistry() {
        return mViewModelRegistry;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModelRegistry.clear();
    }
}
