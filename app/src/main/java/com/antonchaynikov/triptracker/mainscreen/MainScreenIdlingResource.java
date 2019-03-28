package com.antonchaynikov.triptracker.mainscreen;

import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.test.espresso.IdlingResource;

class MainScreenIdlingResource implements IdlingResource {

    private static final String TAG = MainScreenIdlingResource.class.getCanonicalName();
    private final int mResourceCount;
    private AtomicInteger mBusyResourceCount = new AtomicInteger();
    private String mName;

    private ResourceCallback mCallback;

    MainScreenIdlingResource(@NonNull String resourceName, int resourceCount) {
        mResourceCount = resourceCount > 0 ? resourceCount : 0;
        mName = resourceName;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isIdleNow() {
        return mBusyResourceCount.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

    void set() {
        mBusyResourceCount.set(mResourceCount);
    }

    void onItemEmitted() {
        if (mBusyResourceCount.get() > 0) {
            mBusyResourceCount.decrementAndGet();
        } else {
            if (mCallback != null) {
                mCallback.onTransitionToIdle();
            }
        }
    }
}
