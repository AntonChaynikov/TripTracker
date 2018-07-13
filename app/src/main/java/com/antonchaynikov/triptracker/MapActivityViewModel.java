package com.antonchaynikov.triptracker;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class MapActivityViewModel {

    public final static boolean BUTTON_TEXT_START = true;

    private final static String TAG = "MapActivityViewModel";

    private PublishSubject<Boolean> mButtonTextState;
    private PublishSubject<String> mEditTextState;
    private BehaviorSubject<Boolean> mAskPermissionEvent;

    private LocationSource mLocationSource;
    private Mapper mMapper;

    private boolean mPermissionGranted;
    private boolean mReceivingCoordinates;

    public MapActivityViewModel(LocationSource locationSource, Mapper mapper, boolean permissionGranted) {
        mButtonTextState = PublishSubject.create();
        mEditTextState = PublishSubject.create();
        mAskPermissionEvent = BehaviorSubject.create();

        mMapper = mapper;
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

    public Observable<String> getEditTextChangeEvent() {
        return mEditTextState;
    }

    public void stopUpdates() {
        if (mReceivingCoordinates) {
            mLocationSource.toggleLocationUpdates();
        }
    }

    public void onPermissionRequestResult(boolean result) {
        mPermissionGranted = result;
    }

    public void onMapReady(GoogleMap googleMap) {
        mMapper.onMapReady(googleMap);
    }

    private void onLocationUpdated(Location location) {
        mEditTextState.onNext(location.toString());
        if (mMapper.isReady()) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMapper.clear();
            mMapper.addMarker(latLng);
            mMapper.moveCamera(latLng);
        }
    }

    private Disposable subscribeToLocationUpdates() {
        return mLocationSource.getLocation()
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
