package com.antonchaynikov.triptracker.trips;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.repository.firestore.FireStoreDB;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;
import com.antonchaynikov.triptracker.viewmodel.ViewModelActivity;
import com.antonchaynikov.triptracker.viewmodel.ViewModelFactory;
import com.antonchaynikov.triptracker.viewmodel.ViewModelProviders;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;

public class TripsListActivity extends ViewModelActivity {

    private RecyclerView mRecyclerView;
    private View vProgressBar;
    private TextView tvNoTrips;
    private TripsListViewModel mViewModel;

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
        initViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.onStart();
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

    private void initViewModel() {

        ViewModelFactory factory = new ViewModelFactory() {
            @Override
            public <T extends BasicViewModel> T create(@NonNull Class<T> clazz) {
                return (T) new TripsListViewModel(FireStoreDB.getInstance());
            }
        };

        mViewModel = ViewModelProviders.of(this, factory).get(TripsListViewModel.class);
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
        mRecyclerView.setAdapter(new TripsAdapter(trips));
    }
}
