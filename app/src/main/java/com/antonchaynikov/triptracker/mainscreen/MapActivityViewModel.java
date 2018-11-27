package com.antonchaynikov.triptracker.mainscreen;

import android.location.Location;

import com.antonchaynikov.triptracker.data.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

import static com.antonchaynikov.triptracker.mainscreen.MapActivityViewModel.LocationBroadcastStatus.BROADCASTING;
import static com.antonchaynikov.triptracker.mainscreen.MapActivityViewModel.LocationBroadcastStatus.IDLE;

public class MapActivityViewModel extends ViewModel {

    private PublishSubject<Boolean> mRequestLocationPermissionEventBroadcast = PublishSubject.create();
    private BehaviorSubject<LocationBroadcastStatus> mOnLocationBroadcastStatusChangedEventBroadcast = BehaviorSubject.createDefault(IDLE);
    private PublishSubject<LatLng> mNewLocationEventBroadcast = PublishSubject.create();

    private LocationSource mLocationSource;
    private boolean mIsLocationPermissionGranted;
    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    MapActivityViewModel(@NonNull LocationSource locationSource, boolean isLocationPermissionGranted) {
        mIsLocationPermissionGranted = isLocationPermissionGranted;
        mLocationSource = locationSource;
        mSubscriptions.add(subscribeToLocationUpdates(mLocationSource.getLocationUpdates()));
    }

    @VisibleForTesting
    public void setLocationPermissionStatus(boolean isGranted) {
        mIsLocationPermissionGranted = isGranted;
    }

    public Observable<Boolean> getRequestLocationPermissionEventBroadcast() {
        return mRequestLocationPermissionEventBroadcast;
    }

    public Observable<LatLng> getNewLocationEventBroadcast() {
        return mNewLocationEventBroadcast;
    }

    public Observable<LocationBroadcastStatus>getOnLocationBroadcastStatusChangedEventBroadcast() {
        return mOnLocationBroadcastStatusChangedEventBroadcast;
    }

    public void onStartTripButtonClick() {
        if (mIsLocationPermissionGranted) {
            if (mLocationSource.isUpdateEnabled()) {
                mLocationSource.startUpdates();
                mOnLocationBroadcastStatusChangedEventBroadcast.onNext(BROADCASTING);
            } else {

            }
        } else {
            mRequestLocationPermissionEventBroadcast.onNext(true);
        }
    }

    public void clear() {
        mSubscriptions.clear();
    }

    private Disposable subscribeToLocationUpdates(@NonNull Observable<Location> locations) {
        return locations.subscribe(location -> mNewLocationEventBroadcast.onNext(
                new LatLng(location.getLatitude(), location.getLongitude())));
    }

    enum LocationBroadcastStatus {
        BROADCASTING, IDLE
    }
}
