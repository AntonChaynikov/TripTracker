package com.antonchaynikov.triptracker.mainscreen;

import android.location.Location;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
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
    private BehaviorSubject<LocationBroadcastStatus> mOnLocationBroadcastStatusChangedEventBroadcast;
    private PublishSubject<LatLng> mNewLocationEventBroadcast = PublishSubject.create();
    private PublishSubject<Integer> mShowSnackbarMessageBroadcast = PublishSubject.create();

    private LocationSource mLocationSource;
    private boolean mIsLocationPermissionGranted;
    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    private LocationBroadcastStatus mLocationBroadcastStatus;

    MapActivityViewModel(@NonNull LocationSource locationSource, boolean isLocationPermissionGranted) {
        mIsLocationPermissionGranted = isLocationPermissionGranted;
        mLocationSource = locationSource;
        mSubscriptions.add(subscribeToLocationUpdates(mLocationSource.getLocationUpdates()));
        if (mLocationSource.isLocationsUpdateEnabled()) {
            mLocationBroadcastStatus = BROADCASTING;
        } else {
            mLocationBroadcastStatus = IDLE;
        }
        mOnLocationBroadcastStatusChangedEventBroadcast = BehaviorSubject.createDefault(mLocationBroadcastStatus);
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

    public Observable<Integer> getShowSnackbarMessageBroadcast() {
        return mShowSnackbarMessageBroadcast;
    }

    public void onStartTripButtonClick() {
        if (mIsLocationPermissionGranted) {
            if (mLocationSource.isUpdateAvailable()) {
                mLocationSource.startUpdates();
                mLocationBroadcastStatus = BROADCASTING;
                mOnLocationBroadcastStatusChangedEventBroadcast.onNext(mLocationBroadcastStatus);
            } else {
                showSnackbarMessage(R.string.snackbar_location_service_unavailable);
            }
        } else {
            mRequestLocationPermissionEventBroadcast.onNext(true);
        }
    }

    public void onFinishTripButtonClick() {
        mLocationSource.stopUpdates();
        mLocationBroadcastStatus = IDLE;
        mOnLocationBroadcastStatusChangedEventBroadcast.onNext(mLocationBroadcastStatus);
    }

    public void clear() {
        mSubscriptions.clear();
    }

    private Disposable subscribeToLocationUpdates(@NonNull Observable<Location> locations) {
        return locations.subscribe(location -> mNewLocationEventBroadcast.onNext(
                new LatLng(location.getLatitude(), location.getLongitude())));
    }

    private void showSnackbarMessage(@StringRes int stringId) {
        mShowSnackbarMessageBroadcast.onNext(stringId);
    }

    enum LocationBroadcastStatus {
        BROADCASTING, IDLE
    }
}
