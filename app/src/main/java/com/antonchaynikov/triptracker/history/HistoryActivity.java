package com.antonchaynikov.triptracker.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.application.TripApplication;
import com.antonchaynikov.triptracker.data.repository.firestore.FireStoreDB;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter;
import com.antonchaynikov.triptracker.viewmodel.TripStatistics;
import com.antonchaynikov.triptracker.viewmodel.ViewModelActivity;
import com.antonchaynikov.triptracker.viewmodel.ViewModelFactory;
import com.antonchaynikov.triptracker.viewmodel.ViewModelProviders;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import io.reactivex.disposables.CompositeDisposable;

public class HistoryActivity extends ViewModelActivity implements OnMapReadyCallback {

    private static final String EXTRA_TRIP_START_DATE = "com.antonchaynikov.triptracker.history.EXTRA_TRIP_START_DATE";
    private static final String IDLING_RES_NAME = "com.antonchaynikov.triptracker.history.HistoryActivity";

    @Inject
    HistoryViewModel mViewModel;

    private ProgressBar mProgressBar;
    private View vgStatisticsLayout;
    private View vgMapFrame;

    private TextView tvStartDate;
    private TextView tvDuration;
    private TextView tvSpeed;
    private TextView tvDistance;

    private GoogleMap mGoogleMap;

    private CountingIdlingResource mIdlingResource = new CountingIdlingResource(IDLING_RES_NAME);

    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    public static Intent getStartIntent(@NonNull Context context, long tripStartDate) {
        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra(EXTRA_TRIP_START_DATE, tripStartDate);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mProgressBar = findViewById(R.id.pb_history);
        vgStatisticsLayout = findViewById(R.id.vg_layout_statistics);
        vgMapFrame = findViewById(R.id.vg_history_activity_map_frame);

        tvStartDate = findViewById(R.id.tv_statistics_extended_start_date);
        tvDuration = findViewById(R.id.tv_statistics_extended_duration);
        tvSpeed = findViewById(R.id.tv_statistics_speed);
        tvDistance = findViewById(R.id.tv_statistics_distance);

        addMapFragment();

        long tripStartDate = getIntent().getLongExtra(EXTRA_TRIP_START_DATE, -1);
        if (tripStartDate == -1) {
            throw new IllegalArgumentException("Should have used getStartIntent(context, long)");
        }
        ((TripApplication) getApplication()).injectHistoryActivityDependencies(this, tripStartDate);

        initViewModel();
    }

    private void initViewModel() {
        mSubscriptions.add(mViewModel.getShowProgressBarEventBroadcast().subscribe(this::setProgressBarVisible));
        mSubscriptions.add(mViewModel.getMapOptionsObservable().subscribe(this::onMapOptionsLoaded));
        mSubscriptions.add(mViewModel.getStatisticsObservable().subscribe(this::onTripStatisticsLoaded));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    private void addMapFragment() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.vg_history_activity_map_frame, mapFragment)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        mIdlingResource.increment();
        mIdlingResource.increment();
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

    @VisibleForTesting
    public IdlingResource getIdlingResource() {
        return mIdlingResource;
    }

    private void onMapOptionsLoaded(@NonNull MapOptions mapOptions) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            com.antonchaynikov.triptracker.history.MarkerOptions markerOptions = mapOptions.getMarkerOptions();
            LatLng coords = new LatLng(markerOptions.getCoordinatesLatitude(), markerOptions.getCoordinatesLongitude());
            CameraPosition campos = new CameraPosition.Builder()
                    .target(coords)
                    .zoom(markerOptions.getCameraZoomLevel())
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(campos));
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(coords));

            mGoogleMap.addPolyline(mapOptions.getPolylineOptions());
        }
        mIdlingResource.decrement();
    }

    private void onTripStatisticsLoaded(@NonNull TripStatistics statistics) {
        tvStartDate.setText(statistics.getStartDate());
        tvDuration.setText(statistics.getDuration());
        tvSpeed.setText(statistics.getSpeed());
        tvDistance.setText(statistics.getDistance());
        mIdlingResource.decrement();
    }

    private void setProgressBarVisible(boolean isVisible) {
        if (isVisible) {
            mProgressBar.setVisibility(View.VISIBLE);
            vgMapFrame.setVisibility(View.GONE);
            vgStatisticsLayout.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            vgMapFrame.setVisibility(View.VISIBLE);
            vgStatisticsLayout.setVisibility(View.VISIBLE);
        }
    }
}
