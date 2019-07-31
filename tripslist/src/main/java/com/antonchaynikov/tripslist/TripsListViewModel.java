package com.antonchaynikov.tripslist;

import androidx.annotation.NonNull;

import com.antonchaynikov.core.authentication.Auth;
import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.repository.Repository;
import com.antonchaynikov.core.viewmodel.BasicViewModel;
import com.antonchaynikov.core.viewmodel.StatisticsFormatter;
import com.antonchaynikov.core.viewmodel.TripStatistics;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

public class TripsListViewModel extends BasicViewModel implements TripsAdapter.TripsProvider, TripsAdapter.ItemClickListener {

    private static final String TAG = TripsListViewModel.class.getCanonicalName();

    private PublishSubject<Boolean> mShowEmptyListMessageEvent = PublishSubject.create();
    private PublishSubject<Boolean> mTripsDataLoadedEvent = PublishSubject.create();
    private PublishSubject<Boolean> mNavigateToMainScreen = PublishSubject.create();
    private PublishSubject<Long> mNavigateToDetailScreen = PublishSubject.create();

    private Repository mRepository;
    private Auth mAuth;
    private StatisticsFormatter mStatisticsFormatter;
    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    private List<Trip> mTripsList;

    public TripsListViewModel(@NonNull Repository repository, @NonNull Auth auth, @NonNull StatisticsFormatter statsFormatter) {
        mRepository = repository;
        mAuth = auth;
        mStatisticsFormatter = statsFormatter;
    }

    void onStart() {
        if (!mAuth.isSignedIn()) {
            onLogout();
        } else {
            loadData();
        }
    }

    void onLogoutButtonClicked() {
        logout();
    }

    Observable<Boolean> getEmptyListEventObservable() {
        return mShowEmptyListMessageEvent;
    }

    Observable<Boolean> getTripsDataLoadedEventObservable() {
        return mTripsDataLoadedEvent;
    }

    Observable<Boolean> getNavigateToMainScreenObservable() {
        return mNavigateToMainScreen;
    }

    Observable<Long> getNavigateToDetailScreenObservable() {
        return mNavigateToDetailScreen;
    }

    @Override
    public TripsListItemModel getTrip(int position) {
        TripStatistics statistics = mStatisticsFormatter.formatTrip(mTripsList.get(position));
        return getListItemModel(statistics);
    }

    @Override
    public int getItemCount() {
        return mTripsList == null ? 0 : mTripsList.size();
    }

    @Override
    public void onItemClicked(int position) {
        mNavigateToDetailScreen.onNext(mTripsList.get(position).getStartDate());
    }

    private void loadData() {
        mShowProgressBarEventBroadcast.onNext(true);
        mSubscriptions.add(mRepository
                .getAllTrips()
                .subscribe(this::onTripsLoaded));
    }

    private void onTripsLoaded(@NonNull List<Trip> trips) {
        if (trips.isEmpty()) {
            mShowEmptyListMessageEvent.onNext(true);
        }
        mTripsList = trips;
        mTripsDataLoadedEvent.onNext(true);
        mShowProgressBarEventBroadcast.onNext(false);
    }

    private TripsListItemModel getListItemModel(TripStatistics statistics) {
        return new TripsListItemModel(
                statistics.getStartDate(),
                statistics.getDuration(),
                statistics.getSpeed(),
                statistics.getDistance()
        );
    }

    private void logout() {
        mAuth.signOut();
        onLogout();
    }

    private void onLogout() {
        mNavigateToMainScreen.onNext(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mSubscriptions.dispose();
    }
}
