package com.antonchaynikov.triptracker.mainscreen;

import android.util.Log;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.antonchaynikov.triptracker.mainscreen.uistate.MapActivityUiState;
import com.antonchaynikov.triptracker.utils.StringUtils;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

import static com.antonchaynikov.triptracker.mainscreen.uistate.MapActivityUiState.State.IDLE;
import static com.antonchaynikov.triptracker.mainscreen.uistate.MapActivityUiState.State.STARTED;

class TripViewModel extends BasicViewModel {

    private static final String TAG = TripViewModel.class.getCanonicalName();

    private BehaviorSubject<MapActivityUiState> mUiStateChangeEventObservable;
    private PublishSubject<Boolean> mAskLocationPermissionEventObservable = PublishSubject.create();
    private BehaviorSubject<TripStatistics> mTripStatisticsStreamObservable;

    private PublishSubject<MapOptions> mMapOptionsObservable = PublishSubject.create();

    private TripManager mTripManager;

    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    private boolean mIsLocationPermissionGranted;

    private MapActivityUiState mUiState;

    TripViewModel(@NonNull TripManager tripManager, boolean isLocationPermissionGranted) {
        mUiState = MapActivityUiState.getDefaultState();
        mUiStateChangeEventObservable = BehaviorSubject.createDefault(mUiState);
        mTripStatisticsStreamObservable = BehaviorSubject.createDefault(TripStatistics.getDefaultStatistics());
        mIsLocationPermissionGranted = isLocationPermissionGranted;
        mTripManager = tripManager;
        mSubscriptions.add(mTripManager.getTripUpdatesStream().subscribe(this::handleTripUpdate));
        mSubscriptions.add(mTripManager.getCoordinatesStream().subscribe(this::handleLocationUpdate));
    }

    Observable<MapActivityUiState> getUiStateChangeEventObservable() {
        return mUiStateChangeEventObservable;
    }

    Observable<Boolean> getAskLocationPermissionEventObserver() {
        return mAskLocationPermissionEventObservable;
    }

    Observable<MapOptions> getMapOptionsObservable() {
        return mMapOptionsObservable;
    }

    Observable<TripStatistics> getTripStatisticsStreamObservable() {
        return mTripStatisticsStreamObservable;
    }

    void onLocationPermissionUpdate(boolean isPermissionGranted) {
        mIsLocationPermissionGranted = isPermissionGranted;
    }

    void onActionButtonClicked() {
        if (mUiState.getState() == IDLE) {
            if (mIsLocationPermissionGranted) {
                startTrip();
            } else {
                mAskLocationPermissionEventObservable.onNext(true);
            }
        } else {
            stopTrip();
        }
    }

    boolean isTripStarted() {
        return mUiState.getState() == STARTED;
    }

    private void startTrip() {
        mSubscriptions.add(mTripManager.startTrip().subscribe(() ->
                mUiStateChangeEventObservable.onNext(mUiState.transform(STARTED))
        ));
    }

    private void stopTrip() {
        mSubscriptions.add(mTripManager.finishTrip().subscribe(() -> {
            mUiStateChangeEventObservable.onNext(mUiState.transform(IDLE));
            mMapOptionsObservable.onNext(new MapOptions(true));
        }));
        mTripStatisticsStreamObservable.onNext(TripStatistics.getDefaultStatistics());
    }

    private void handleTripUpdate(@NonNull Trip trip) {
        double distance = trip.getDistance();
        double speed = trip.getSpeed();
        mTripStatisticsStreamObservable.onNext(new TripStatistics(
                StringUtils.numToFormattedString(speed, true),
                StringUtils.numToFormattedString(distance, true)));
    }

    private void handleLocationUpdate(@NonNull TripCoordinate tripCoordinate) {
        mMapOptionsObservable.onNext(new MapOptions(tripCoordinate));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "Destroying this ViewModel instance");
        mSubscriptions.dispose();
    }
}


