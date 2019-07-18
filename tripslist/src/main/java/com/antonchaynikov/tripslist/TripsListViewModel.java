package com.antonchaynikov.tripslist;

import android.util.Log;

import androidx.annotation.NonNull;

import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.repository.Repository;
import com.antonchaynikov.core.viewmodel.BasicViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

public class TripsListViewModel extends BasicViewModel {

    private PublishSubject<Boolean> mShowEmptyListMessageEvent = PublishSubject.create();
    private PublishSubject<List<Trip>> mTripsListSubject = PublishSubject.create();
    private PublishSubject<Boolean> mNavigateToMainScreen = PublishSubject.create();

    private Repository mRepository;
    private FirebaseAuth mFirebaseAuth;
    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    public TripsListViewModel(@NonNull Repository repository, @NonNull FirebaseAuth firebaseAuth) {
        mRepository = repository;
        mFirebaseAuth = firebaseAuth;
    }

    void onStart() {
        mShowProgressBarEventBroadcast.onNext(true);
        mSubscriptions.add(mRepository
                .getAllTrips()
                .subscribe(this::onTripsLoaded));
    }

    void onLogoutButtonClicked() {
        mFirebaseAuth.signOut();
        mNavigateToMainScreen.onNext(true);
    }

    Observable<Boolean> getEmptyListEventObservable() {
        return mShowEmptyListMessageEvent;
    }

    Observable<List<Trip>> getTripListObservable() {
        return mTripsListSubject;
    }

    Observable<Boolean> getNavigateToMainScreenObservable() {
        return mNavigateToMainScreen;
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
