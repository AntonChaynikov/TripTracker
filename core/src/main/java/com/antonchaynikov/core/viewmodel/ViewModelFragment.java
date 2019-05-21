package com.antonchaynikov.core.viewmodel;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.antonchaynikov.core.Testable;

public class ViewModelFragment extends Fragment implements Testable {
    private ViewModelRegistry mViewModelRegistry;
    protected boolean isInTestMode;

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

    @Override
    public void setTestMode(boolean isInTestMode) {
        this.isInTestMode = isInTestMode;
    }
}
