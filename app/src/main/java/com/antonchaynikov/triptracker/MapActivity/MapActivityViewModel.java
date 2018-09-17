package com.antonchaynikov.triptracker.MapActivity;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class MapActivityViewModel extends ViewModel {

    static final int STATUS_UPDATING = 0;
    static final int STATUS_IDLE = 1;

    private int mStatus = STATUS_IDLE;
    private boolean mIsLocationPermissionGranted;

    public MapActivityViewModel(boolean locationPermissionGranted) {
        mLocationUpdateStatusChangeEvent.onNext(mStatus);
        mIsLocationPermissionGranted = locationPermissionGranted;
    }

    private BehaviorSubject<Integer> mLocationUpdateStatusChangeEvent = BehaviorSubject.create();
    private BehaviorSubject mLocationPermissionRequestEvent = BehaviorSubject.create();

    public void toggleCoordinatesUpdate() {
        if (mStatus == STATUS_UPDATING) {
            mStatus = STATUS_IDLE;
        } else {
            mStatus = STATUS_UPDATING;
        }
        mLocationUpdateStatusChangeEvent.onNext(mStatus);
    }

    @NonNull
    public Observable<Integer> getLocationUpdateStatusChangeEvent() {
        return mLocationUpdateStatusChangeEvent;
    }

    @NonNull
    public Observable getLocationPermissionRequestEvent() {
        return mLocationPermissionRequestEvent;
    }

    public void onLocationPermissionGranted() {
        mIsLocationPermissionGranted = true;
    }

}
