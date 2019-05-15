package com.antonchaynikov.tripshistory;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.antonchaynikov.core.viewmodel.TripStatistics;
import com.antonchaynikov.core.viewmodel.ViewModelFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.CompositeDisposable;

public class HistoryFragment extends ViewModelFragment implements OnMapReadyCallback {
    private static final String IDLING_RES_NAME = "HistoryFragment";
    private static final long NO_START_DATE_ARG = -1;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);

        mProgressBar = view.findViewById(R.id.pb_history);
        vgStatisticsLayout = view.findViewById(R.id.vg_layout_statistics);
        vgMapFrame = view.findViewById(R.id.vg_history_activity_map_frame);

        tvStartDate = view.findViewById(R.id.tv_statistics_extended_start_date);
        tvDuration = view.findViewById(R.id.tv_statistics_extended_duration);
        tvSpeed = view.findViewById(R.id.tv_statistics_speed);
        tvDistance = view.findViewById(R.id.tv_statistics_distance);

        addMapFragment();

        initViewModel();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    public long getTripStartDate() {
        return readStartDateArgument();
    }

    private long readStartDateArgument() {
        long tripStartDate = getArguments().getLong(getString(R.string.tripStartDate), NO_START_DATE_ARG);
        if (tripStartDate == NO_START_DATE_ARG) {
            throw new IllegalStateException("Trips start date should have been provided as an argument");
        }
        return tripStartDate;
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
        getChildFragmentManager()
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
            MarkerOptions markerOptions = mapOptions.getMarkerOptions();
            LatLng coords = new LatLng(markerOptions.getCoordinatesLatitude(), markerOptions.getCoordinatesLongitude());
            CameraPosition campos = new CameraPosition.Builder()
                    .target(coords)
                    .zoom(markerOptions.getCameraZoomLevel())
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(campos));
            mGoogleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
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
