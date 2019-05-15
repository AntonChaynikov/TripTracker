package com.antonchaynikov.core.viewmodel;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("Registered")
public class ViewModelActivity extends AppCompatActivity {

    private ViewModelRegistry mViewModelRegistry;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModelRegistry = (ViewModelRegistry) getLastCustomNonConfigurationInstance();
        if (mViewModelRegistry == null) {
            mViewModelRegistry = new ViewModelRegistry();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mViewModelRegistry;
    }

    ViewModelRegistry getViewModelRegistry() {
        return mViewModelRegistry;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mViewModelRegistry != null && !isChangingConfigurations()) {
            mViewModelRegistry.clear();
        }
    }
}
