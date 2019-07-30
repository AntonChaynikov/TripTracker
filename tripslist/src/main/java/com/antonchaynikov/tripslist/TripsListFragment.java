package com.antonchaynikov.tripslist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.antonchaynikov.core.injection.Injector;
import com.antonchaynikov.core.viewmodel.ViewModelFragment;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class TripsListFragment extends ViewModelFragment {
    private static final String IDLING_RES_NAME = "TripsListFragment";
    private static final String TAG = TripsListViewModel.class.getCanonicalName();

    @Inject
    TripsListViewModel mViewModel;
    @Inject
    NavigationTripsList mNavigation;

    private RecyclerView mRecyclerView;
    private View vProgressBar;
    private TextView tvNoTrips;

    private CountingIdlingResource mIdlingResource = new CountingIdlingResource(IDLING_RES_NAME);
    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trips_list_layout, container, false);
        mRecyclerView = view.findViewById(R.id.rv_trips_list);
        vProgressBar = view.findViewById(R.id.pb_trips_list);
        tvNoTrips = view.findViewById(R.id.tv_no_trips_trips_list);
        Injector.inject(this);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initViewModel();
        mViewModel.onStart();
        if (mIdlingResource.isIdleNow()) {
            mIdlingResource.increment();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mSubscriptions.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_trip_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_trip_action_logout) {
            mViewModel.onLogoutButtonClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @VisibleForTesting
    public IdlingResource getIdlingResource() {
        return mIdlingResource;
    }

    private void initViewModel() {
        mSubscriptions.add(mViewModel.getEmptyListEventObservable().subscribe(event -> handleEmptyTripsList()));
        mSubscriptions.add(mViewModel.getShowProgressBarEventBroadcast().subscribe(this::handleShowProgressDialogEvent));
        mSubscriptions.add(mViewModel.getTripsDataLoadedEventObservable().subscribe(event -> onTripsListLoaded()));
        mSubscriptions.add(mViewModel.getNavigateToMainScreenObservable().subscribe(this::navigateToMainScreen));
        mSubscriptions.add(mViewModel.getNavigateToDetailScreenObservable().subscribe(this::navigateToDetailScreen));
    }

    private void handleEmptyTripsList() {
        tvNoTrips.setVisibility(View.VISIBLE);
    }

    private void handleShowProgressDialogEvent(boolean visible) {
        int visibilityMode = visible ? View.VISIBLE : View.GONE;
        vProgressBar.setVisibility(visibilityMode);
    }

    private void navigateToMainScreen(boolean shouldNavigate) {
        if (shouldNavigate) {
            mNavigation.navigateOnLogoutTripsList(this);
        }
    }

    private void navigateToDetailScreen(long id) {
        mNavigation.navigateOnItemClicked(this, id);
    }

    private void onTripsListLoaded() {
        if (tvNoTrips.getVisibility() == View.VISIBLE) {
            tvNoTrips.setVisibility(View.GONE);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new TripsAdapter(mViewModel, mViewModel));
        mIdlingResource.decrement();
    }
}
