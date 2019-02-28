package com.antonchaynikov.triptracker.mainscreen;

import android.util.Log;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.antonchaynikov.triptracker.mainscreen.uistate.TripUiState;
import com.antonchaynikov.triptracker.utils.StringUtils;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter;
import com.antonchaynikov.triptracker.viewmodel.TripStatistics;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

import static com.antonchaynikov.triptracker.mainscreen.uistate.TripUiState.State.IDLE;
import static com.antonchaynikov.triptracker.mainscreen.uistate.TripUiState.State.STARTED;

class TripViewModel extends BasicViewModel {

    private static final String TAG = TripViewModel.class.getCanonicalName();

    private BehaviorSubject<TripUiState> mUiStateChangeEventObservable;
    private PublishSubject<Boolean> mAskLocationPermissionEventObservable = PublishSubject.create();
    private PublishSubject<Boolean> mGoToStatisticsObservable = PublishSubject.create();
    private PublishSubject<Boolean> mLogoutObservable = PublishSubject.create();
    private BehaviorSubject<TripStatistics> mTripStatisticsStreamObservable;
    private PublishSubject<MapOptions> mMapOptionsObservable = PublishSubject.create();
    private PublishSubject<Long> mProceedToSummaryObservable = PublishSubject.create();

    private TripManager mTripManager;
    private FirebaseAuth mFirebaseAuth;
    private StatisticsFormatter mStatisticsFormatter;

    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    private boolean mIsLocationPermissionGranted;

    private TripUiState mUiState;

    TripViewModel(@NonNull TripManager tripManager,
                  @NonNull FirebaseAuth firebaseAuth,
                  @NonNull StatisticsFormatter statisticsFormatter,
                  boolean isLocationPermissionGranted) {
        mUiState = TripUiState.getDefaultState();
        mUiStateChangeEventObservable = BehaviorSubject.createDefault(mUiState);
        mTripStatisticsStreamObservable = BehaviorSubject.createDefault(TripStatistics.getDefaultStatistics());
        mIsLocationPermissionGranted = isLocationPermissionGranted;
        mStatisticsFormatter = statisticsFormatter;
        mTripManager = tripManager;
        mFirebaseAuth = firebaseAuth;
        mSubscriptions.add(mTripManager.getTripUpdatesStream().subscribe(this::handleTripUpdate));
        mSubscriptions.add(mTripManager.getCoordinatesStream().subscribe(this::handleLocationUpdate));
        mSubscriptions.add(mTripManager.getGeoloactionAvailabilityChangeObservable().subscribe(this::handleGeolocationAvailabilityChange));
    }

    Observable<TripUiState> getUiStateChangeEventObservable() {
        return mUiStateChangeEventObservable;
    }

    Observable<Boolean> getAskLocationPermissionEventObservable() {
        return mAskLocationPermissionEventObservable;
    }

    Observable<MapOptions> getMapOptionsObservable() {
        return mMapOptionsObservable;
    }

    Observable<TripStatistics> getTripStatisticsStreamObservable() {
        return mTripStatisticsStreamObservable;
    }

    Observable<Boolean> getGotToStatisticsObservable() {
        return mGoToStatisticsObservable;
    }

    Observable<Boolean> getLogoutObservable() {
        return mLogoutObservable;
    }

    Observable<Long> getProceedToSummaryObservable() {
        return mProceedToSummaryObservable;
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

    void onStatisticsButtonClicked() {
        mGoToStatisticsObservable.onNext(true);
    }

    void onLogoutButtonClicked() {
        if (mUiState.getState() == TripUiState.State.STARTED) {
            mSubscriptions.add(mTripManager.finishTrip().subscribe(() -> {
                mFirebaseAuth.signOut();
                mLogoutObservable.onNext(true);
            }));
        } else {
            mFirebaseAuth.signOut();
            mLogoutObservable.onNext(true);
        }
    }

    boolean isTripStarted() {
        return mUiState.getState() == STARTED;
    }

    private void handleGeolocationAvailabilityChange(boolean isAvailable) {
        if (isAvailable) {
            showSnackbarMessage(R.string.message_geolocation_available);
        } else {
            showSnackbarMessage(R.string.message_geolocation_unavailable);
        }
    }

    private void startTrip() {
        mSubscriptions.add(mTripManager.startTrip()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe(() ->
                mUiStateChangeEventObservable.onNext(mUiState.transform(STARTED))
        ));
    }

    private void stopTrip() {
        long tripStartDate = mTripManager.getCurrentTrip().getStartDate();
        mSubscriptions.add(mTripManager.finishTrip().subscribe(() -> {
            mUiStateChangeEventObservable.onNext(mUiState.transform(IDLE));
            mMapOptionsObservable.onNext(new MapOptions(true));
            mProceedToSummaryObservable.onNext(tripStartDate);
        }));
        mTripStatisticsStreamObservable.onNext(TripStatistics.getDefaultStatistics());
    }

    private void handleTripUpdate(@NonNull Trip trip) {
        mTripStatisticsStreamObservable.onNext(mStatisticsFormatter.formatTrip(trip));
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
