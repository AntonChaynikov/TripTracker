package com.antonchaynikov.tripslist;

import android.util.Log;

import androidx.annotation.NonNull;

import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.repository.Repository;
import com.antonchaynikov.core.viewmodel.BasicViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

public class TripsListViewModel extends BasicViewModel {

    private PublishSubject<Boolean> mShowEmptyListMessageEvent = PublishSubject.create();
    private PublishSubject<List<Trip>> mTripsListSubject = PublishSubject.create();

    private Repository mRepository;
    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    public TripsListViewModel(@NonNull Repository repository) {
        mRepository = repository;
    }

    void onStart() {
        mShowProgressBarEventBroadcast.onNext(true);
        mSubscriptions.add(mRepository
                .getAllTrips()
                .subscribe(this::onTripsLoaded));
    }

    Observable<Boolean> getEmptyListEventObservable() {
        return mShowEmptyListMessageEvent;
    }

    Observable<List<Trip>> getTripListObservable() {
        return mTripsListSubject;
    }

    private void onTripsLoaded(@NonNull List<Trip> trips) {
        Log.d(TripsListViewModel.class.getCanonicalName(), "onTripsLoaded " + trips.size());
        if (trips.isEmpty()) {
            mShowEmptyListMessageEvent.onNext(true);
        }
        mTripsListSubject.onNext(trips);
        mShowProgressBarEventBroadcast.onNext(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mSubscriptions.dispose();
    }
}
