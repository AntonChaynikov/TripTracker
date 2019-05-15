package com.antonchaynikov.core.data.tripmanager;

import android.location.Location;
import android.util.Log;

import com.antonchaynikov.core.data.location.LocationSource;
import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.model.TripCoordinate;
import com.antonchaynikov.core.data.repository.Repository;

import org.joda.time.DateTime;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public final class TripManager {

    private static volatile TripManager sInstance;

    private PublishSubject<Trip> mTripUpdatesStream = PublishSubject.create();
    private PublishSubject<TripCoordinate> mCoordinatesStream = PublishSubject.create();

    private Repository mRepository;
    private LocationSource mLocationSource;
    private StatisticsCalculator mStatisticsCalculator;
    private volatile Trip mCurrentStartedTrip;

    public TripManager(
            @NonNull Repository repository,
            @NonNull LocationSource locationSource,
            @NonNull StatisticsCalculator statisticsCalculator) {

        mRepository = repository;
        mLocationSource = locationSource;
        mStatisticsCalculator = statisticsCalculator;
        Disposable d = mLocationSource.getLocationsObservable().subscribe(this::handleCoordinatesUpdate);
    }

    public static TripManager getInstance(
            @NonNull Repository repository,
            @NonNull LocationSource locationSource,
            @NonNull StatisticsCalculator statisticsCalculator) {

        if (sInstance == null) {
            synchronized (TripManager.class) {
                if (sInstance == null) {
                    sInstance = new TripManager(repository, locationSource, statisticsCalculator);
                }
            }
        }
        return sInstance;
    }

    public static void resetInstance() {
        sInstance = null;
    }

    public Observable<Trip> getTripUpdatesStream() {
        return mTripUpdatesStream;
    }

    public Observable<TripCoordinate> getCoordinatesStream() {
        return mCoordinatesStream;
    }

    public Observable<Boolean> getGeolocationAvailabilityChangeObservable() {
        return mLocationSource.getGeolocationAvailabilityObservable();
    }

    public Completable startTrip() {
        Trip trip = new Trip(DateTime.now().getMillis());
        return mRepository
                .addTrip(trip)
                .doOnComplete(() -> onTripStarted(trip));
    }

    public Completable finishTrip() {
        mCurrentStartedTrip.setEndDate(DateTime.now().getMillis());
        return mRepository
                .updateTrip(mCurrentStartedTrip)
                .doOnComplete(this::onTripFinished);
    }

    public Trip getCurrentTrip() {
        return mCurrentStartedTrip;
    }

    private void onTripStarted(@NonNull Trip trip) {
        Log.d("TripManager", "onTripStarted");
        mCurrentStartedTrip = trip;
        mLocationSource.startUpdates();
    }

    private void onTripFinished() {
        mLocationSource.finishUpdates();
        mCurrentStartedTrip = null;
    }

    private void handleCoordinatesUpdate(@NonNull Location coordinate) {
        TripCoordinate tripCoordinate = new TripCoordinate(coordinate.getTime(), coordinate.getLatitude(), coordinate.getLongitude());
        mCoordinatesStream.onNext(tripCoordinate);
        mRepository.addCoordinate(tripCoordinate, mCurrentStartedTrip);
        mStatisticsCalculator.addCoordinate(coordinate);
        mTripUpdatesStream.onNext(mCurrentStartedTrip.updateStatistics(
                mStatisticsCalculator.getDistance(),
                mStatisticsCalculator.getSpeed()));
    }
}
