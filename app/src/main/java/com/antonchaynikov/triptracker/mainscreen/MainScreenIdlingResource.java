package com.antonchaynikov.triptracker.mainscreen;

import androidx.annotation.NonNull;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

class MainScreenIdlingResource implements IdlingResource {

    private int mResourceCount;
    private int mBusyResourceCount;
    private CountingIdlingResource mIdlingResource;

    MainScreenIdlingResource(@NonNull String resourceName, int resourceCount) {
        mIdlingResource = new CountingIdlingResource(resourceName);
        mResourceCount = resourceCount > 0 ? resourceCount : 0;
    }

    @Override
    public String getName() {
        return mIdlingResource.getName();
    }

    @Override
    public boolean isIdleNow() {
        return mBusyResourceCount == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mIdlingResource.registerIdleTransitionCallback(callback);
    }

    void set() {
        for (int i = 0; i <= mResourceCount; i++) {
            mIdlingResource.increment();
        }
        mBusyResourceCount = mResourceCount;
    }

    void onItemEmitted() {
        if (mBusyResourceCount > 0) {
            mIdlingResource.decrement();
            mBusyResourceCount--;
        }
    }
}
