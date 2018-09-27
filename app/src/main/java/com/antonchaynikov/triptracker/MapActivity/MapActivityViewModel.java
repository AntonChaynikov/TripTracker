package com.antonchaynikov.triptracker.MapActivity;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class MapActivityViewModel extends ViewModel {

    static final int STATUS_UPDATING = 0;
    static final int STATUS_IDLE = 1;

    private LocationSource mLocationSource;

    private int mStatus = STATUS_IDLE;
    private boolean mIsLocationPermissionGranted;

    private BehaviorSubject<Integer> mLocationUpdateStatusChangeEvent = BehaviorSubject.create();
    private BehaviorSubject<Object> mLocationPermissionRequestEvent = BehaviorSubject.create();
    private PublishSubject<LatLng> mNewLocationReceivedEvent = PublishSubject.create();

    private Object mStubBroadcast = new Object();

    public MapActivityViewModel(@NonNull LocationSource locationSource, boolean locationPermissionGranted) {
        mLocationUpdateStatusChangeEvent.onNext(mStatus);
        mIsLocationPermissionGranted = locationPermissionGranted;
        if (!mIsLocationPermissionGranted) {
            mLocationPermissionRequestEvent.onNext(mStubBroadcast);
        }
        mLocationSource = locationSource;
        mLocationSource.getLocationUpdates().subscribe(location -> {
            if (location != null) {

            }
        });
    }

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
    public Observable<Object> getLocationPermissionRequestEvent() {
        return mLocationPermissionRequestEvent;
    }

    @NonNull
    public PublishSubject<LatLng> getNewLocationReceivedEvent() {
        return mNewLocationReceivedEvent;
    }

    public void onLocationPermissionGranted() {
        mIsLocationPermissionGranted = true;
    }

}
