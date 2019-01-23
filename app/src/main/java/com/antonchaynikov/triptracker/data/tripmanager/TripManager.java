package com.antonchaynikov.triptracker.data.tripmanager;

import android.location.Location;
import android.util.Log;

import com.antonchaynikov.triptracker.data.location.LocationSource;
import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.repository.Repository;

import org.joda.time.DateTime;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class TripManager {

    private static volatile TripManager sInstance;

    private PublishSubject<Trip> mTripUpdatesStream = PublishSubject.create();
    private PublishSubject<TripCoordinate> mCoordinatesStream = PublishSubject.create();

    private Repository mRepository;
    private LocationSource mLocationSource;
    private StatisticsCalculator mStatisticsCalculator;
    private Trip mCurrentStartedTrip;

    private TripManager(
            @NonNull Repository repository,
            @NonNull LocationSource locationSource,
            @NonNull StatisticsCalculator statisticsCalculator) {

        mRepository = repository;
        mLocationSource = locationSource;
        mStatisticsCalculator = statisticsCalculator;
        Disposable d = mLocationSource.getLocationUpdates().subscribe(this::handleCoordinatesUpdate);
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

    @VisibleForTesting
    void resetInstance() {
        sInstance = null;
    }

    public Observable<Trip> getTripUpdatesStream() {
        return mTripUpdatesStream;
    }

    public Observable<TripCoordinate> getCoordinatesStream() {
        return mCoordinatesStream;
    }

    public Completable startTrip() {
        Trip trip = new Trip(DateTime.now().getMillis());
        return mRepository
                .startTrip(trip)
                .doOnComplete(() -> onTripStarted(trip));
    }

    public Completable finishTrip() {
        return mRepository
                .finishTrip(mCurrentStartedTrip, DateTime.now().getMillis())
                .doOnComplete(this::onTripFinished);
    }

    public Trip getCurrentTrip() {
        return mCurrentStartedTrip;
    }

    private void onTripStarted(@NonNull Trip trip) {
        mCurrentStartedTrip = trip;
        mLocationSource.startUpdates();
    }

    private void onTripFinished() {
        mLocationSource.finishUpdates();
        mCurrentStartedTrip = null;
    }

    private void handleCoordinatesUpdate(@NonNull Location coordinate) {
        mCoordinatesStream.onNext(new TripCoordinate(coordinate.getTime(), coordinate.getLatitude(), coordinate.getLongitude()));
        mStatisticsCalculator.addCoordinate(coordinate);
        mTripUpdatesStream.onNext(mCurrentStartedTrip.updateStatistics(
                mStatisticsCalculator.getDistance(),
                mStatisticsCalculator.getSpeed()));
    }
}
