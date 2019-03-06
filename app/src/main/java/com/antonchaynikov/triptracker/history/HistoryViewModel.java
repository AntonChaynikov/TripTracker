package com.antonchaynikov.triptracker.history;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.repository.Repository;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter;
import com.antonchaynikov.triptracker.viewmodel.TripStatistics;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

class HistoryViewModel extends BasicViewModel {

    private Repository mRepository;
    private StatisticsFormatter mStatisticsFormatter;
    private long mStartDate;

    private PublishSubject<TripStatistics> mStatisticsObservable = PublishSubject.create();
    private PublishSubject<MapOptions> mMapOptionsObservable = PublishSubject.create();

    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    HistoryViewModel(@NonNull Repository repository, @NonNull StatisticsFormatter statisticsFormatter, long tripStartDate) {
        mRepository = repository;
        mStatisticsFormatter = statisticsFormatter;
        mStartDate = tripStartDate;
    }

    public Observable<TripStatistics> getStatisticsObservable() {
        return mStatisticsObservable;
    }

    public Observable<MapOptions> getMapOptionsObservable() {
        return mMapOptionsObservable;
    }

    void onStart() {
        mShowProgressBarEventBroadcast.onNext(true);
        mSubscriptions.add(mRepository.getTripByStartDate(mStartDate).subscribe(this::onTripDataLoaded));
        mSubscriptions.add(mRepository.getCoordinatesForTrip(mStartDate).subscribe(this::onTripCoordinatesLoaded));
    }

    private void onTripDataLoaded(@NonNull Trip trip) {
        mStatisticsObservable.onNext(mStatisticsFormatter.formatTrip(trip));
        mShowProgressBarEventBroadcast.onNext(false);
    }

    private void onTripCoordinatesLoaded(@NonNull List<TripCoordinate> coordinates) {
        mMapOptionsObservable.onNext(new MapOptions(coordinates));
    }

    @Override
    public void onCleared() {
        super.onCleared();
        mSubscriptions.dispose();
    }
}
