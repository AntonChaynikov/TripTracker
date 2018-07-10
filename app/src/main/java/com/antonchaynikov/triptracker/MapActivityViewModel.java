package com.antonchaynikov.triptracker;

import android.location.Location;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class MapActivityViewModel {

    public final static boolean BUTTON_TEXT_START = true;

    private final static String TAG = "MapActivityViewModel";

    private PublishSubject<Boolean> mButtonTextState;
    private BehaviorSubject<Boolean> mAskPermissionEvent;
    private BehaviorSubject<Location> mLocationChangeEvent;

    private LocationSource mLocationSource;

    private boolean mPermissionGranted;
    private boolean mReceivingCoordinates;

    public MapActivityViewModel(LocationSource locationSource, boolean permissionGranted) {
        mButtonTextState = PublishSubject.create();
        mAskPermissionEvent = BehaviorSubject.create();
        mLocationChangeEvent = BehaviorSubject.create();

        mLocationSource = locationSource;
        mPermissionGranted = permissionGranted;
        mReceivingCoordinates = mLocationSource.isUpdateEnabled();
        subscribeToLocationUpdates();
    }

    public void onCoordinatesButtonClick() {
        if (mPermissionGranted) {
            mLocationSource.toggleLocationUpdates();
            mReceivingCoordinates = !mReceivingCoordinates;
            if (mReceivingCoordinates) {
                mButtonTextState.onNext(!BUTTON_TEXT_START);
            } else {
                mButtonTextState.onNext(BUTTON_TEXT_START);
            }
        } else {
            mAskPermissionEvent.onNext(true);
        }
    }

    public Observable<Boolean> getButtonTextChangeEvent() {
        return mButtonTextState;
    }

    public Observable<Boolean> getAskPermissionEvent() {
        return mAskPermissionEvent;
    }

    public Observable<Location> getLocationChangedEvent() {
        return mLocationChangeEvent;
    }

    public void stopUpdates() {
        if (mReceivingCoordinates) {
            mLocationSource.toggleLocationUpdates();
        }
    }

    public void onPermissionRequestResult(boolean result) {
        mPermissionGranted = result;
    }

    private void onLocationUpdated(Location location) {
        mLocationChangeEvent.onNext(location);
    }

    private void subscribeToLocationUpdates() {
        mLocationSource.getLocation()
                .doOnNext(new Consumer<Location>() {
                    @Override
                    public void accept(Location location) throws Exception {
                        Log.d(TAG, location.toString());
                        onLocationUpdated(location);
                    }
                })
                .subscribe();
    }

}
