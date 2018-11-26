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
import io.reactivex.subjects.PublishSubject;

public class MapActivityViewModel extends ViewModel {

    private PublishSubject<Boolean> mRequestLocationPermissionEventBroadcast = PublishSubject.create();
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

    public PublishSubject<Boolean> getRequestLocationPermissionEventBroadcast() {
        return mRequestLocationPermissionEventBroadcast;
    }

    public PublishSubject<LatLng> getNewLocationEventBroadcast() {
        return mNewLocationEventBroadcast;
    }

    public void onStartTripButtonClick() {
        if (mIsLocationPermissionGranted) {
            mLocationSource.startUpdates();
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
}
