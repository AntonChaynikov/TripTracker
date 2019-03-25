package com.antonchaynikov.triptracker.trips;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.application.TripApplication;
import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.repository.firestore.FireStoreDB;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;
import com.antonchaynikov.triptracker.viewmodel.ViewModelActivity;
import com.antonchaynikov.triptracker.viewmodel.ViewModelFactory;
import com.antonchaynikov.triptracker.viewmodel.ViewModelProviders;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import io.reactivex.disposables.CompositeDisposable;

public class TripsListActivity extends ViewModelActivity {

    private static final String IDLING_RES_NAME = "com.antonchaynikov.triptracker.trips.TripsListActivity";

    private RecyclerView mRecyclerView;
    private View vProgressBar;
    private TextView tvNoTrips;
    @Inject
    TripsListViewModel mViewModel;

    private CountingIdlingResource mIdlingResource = new CountingIdlingResource(IDLING_RES_NAME);

    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    public static Intent getStartIntent(@NonNull Context context) {
        return new Intent(context, TripsListActivity.class);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trips_list_layout);
        mRecyclerView = findViewById(R.id.rv_trips_list);
        vProgressBar = findViewById(R.id.pb_trips_list);
        tvNoTrips = findViewById(R.id.tv_no_trips_trips_list);
        ((TripApplication) getApplication()).injectTripsListActivityDependencies(this);
        initViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.onStart();
        if (mIdlingResource.isIdleNow()) {
            mIdlingResource.increment();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mSubscriptions.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.onCleared();
    }

    @VisibleForTesting
    public IdlingResource getIdlingResource() {
        return mIdlingResource;
    }

    private void initViewModel() {
        mSubscriptions.add(mViewModel.getEmptyListEventObservable().subscribe(event -> handleEmptyTripsList()));
        mSubscriptions.add(mViewModel.getShowProgressBarEventBroadcast().subscribe(this::handleShowProgressDialogEvent));
        mSubscriptions.add(mViewModel.getTripListObservable().subscribe(this::onTripsListLoaded));
    }

    private void handleEmptyTripsList() {
        tvNoTrips.setVisibility(View.VISIBLE);
    }

    private void handleShowProgressDialogEvent(boolean visible) {
        int visibilityMode = visible ? View.VISIBLE : View.GONE;
        vProgressBar.setVisibility(visibilityMode);
    }

    private void onTripsListLoaded(@NonNull List<Trip> trips) {
        if (tvNoTrips.getVisibility() == View.VISIBLE) {
            tvNoTrips.setVisibility(View.GONE);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TripsAdapter(trips));
        mIdlingResource.decrement();
    }
}
