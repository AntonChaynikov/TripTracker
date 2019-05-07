package com.antonchaynikov.triptracker.mainscreen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.injection.Injector;
import com.antonchaynikov.triptracker.mainscreen.uistate.TripUiState;
import com.antonchaynikov.triptracker.viewmodel.TripStatistics;
import com.antonchaynikov.triptracker.viewmodel.ViewModelFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.espresso.IdlingResource;
import io.reactivex.disposables.CompositeDisposable;

import static com.antonchaynikov.triptracker.mainscreen.TripFragmentDirections.ActionTripFragmentToHistoryFragment;
import static com.antonchaynikov.triptracker.mainscreen.TripFragmentDirections.actionTripFragmentToHistoryFragment;

public class TripFragment extends ViewModelFragment implements View.OnClickListener, OnMapReadyCallback {
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1;

    private static final String IDLING_RES_NAME = "com.antonchaynikov.triptracker.mainscreen.TripFragment";
    private static final String TAG = TripFragment.class.getSimpleName();

    @Inject
    TripViewModel mViewModel;

    private View mRootView;
    private Button mButton;
    private GoogleMap mGoogleMap;
    private TextView tvDistance;
    private TextView tvSpeed;

    private CompositeDisposable mSubscriptions;
    private MainScreenIdlingResource mStatisticsIdlingResource;
    private boolean mPermissionGranted;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionGranted =
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

        Injector.injectTripFragmentDependencies(this, mPermissionGranted);

        mSubscriptions = new CompositeDisposable();

        initViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map, container, false);
        tvDistance = view.findViewById(R.id.tv_statistics_distance);
        tvSpeed = view.findViewById(R.id.tv_statistics_speed);

        mRootView = view.findViewById(R.id.vg_trip_activity_map_frame);
        mButton = view.findViewById(R.id.btn_layout_statistics);
        mButton.setOnClickListener(this);

        setHasOptionsMenu(true);

        addMapFragment();

        return view;
    }

    @VisibleForTesting
    IdlingResource initStatisticsIdlingResource(int itemsToWait) {
        mStatisticsIdlingResource = new MainScreenIdlingResource(IDLING_RES_NAME, itemsToWait);
        return mStatisticsIdlingResource;
    }

    @VisibleForTesting
    void initViewModel() {
        mSubscriptions.add(mViewModel.getAskLocationPermissionEventObservable().subscribe(event -> onLocationPermissionRequested()));
        mSubscriptions.add(mViewModel.getUiStateChangeEventObservable().subscribe(this::onUiStateUpdate));
        mSubscriptions.add(mViewModel.getShowSnackbarMessageBroadcast().subscribe(this::showSnackbarMessage));
        mSubscriptions.add(mViewModel.getMapOptionsObservable().subscribe(this::handleMapOptionsUpdate));
        mSubscriptions.add(mViewModel.getTripStatisticsStreamObservable().subscribe(this::handleStatisticsUpdate));
        mSubscriptions.add(mViewModel.getGotToStatisticsObservable().subscribe(event -> goToStatisticsScreen()));
        mSubscriptions.add(mViewModel.getLogoutObservable().subscribe(event -> logout()));
        mSubscriptions.add(mViewModel.getProceedToSummaryObservable().subscribe(this::goToSummaryScreen));
    }

    @VisibleForTesting
    void setViewModel(TripViewModel viewModel) {
        mViewModel = viewModel;
        System.out.println("testInit");
        Log.d("testInit", "testInit");
        initViewModel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.dispose();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            mPermissionGranted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
        mViewModel.onLocationPermissionUpdate(mPermissionGranted);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    @Override
    public void onClick(View view) {
        if (mStatisticsIdlingResource != null) {
            mStatisticsIdlingResource.set();
        }
        mViewModel.onActionButtonClicked();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_trip_action_statistics: {
                mViewModel.onStatisticsButtonClicked();
                return true;
            }
            case R.id.menu_trip_action_logout: {
                mViewModel.onLogoutButtonClicked();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void addMapFragment() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.vg_trip_activity_map_frame, mapFragment)
                .commit();
    }

    private void onLocationPermissionRequested() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(getActivity(), permissions, ACCESS_FINE_LOCATION_REQUEST_CODE);
    }

    private void onUiStateUpdate(@NonNull TripUiState state) {
        mButton.setText(state.getActionButtomTextId());
    }

    private void handleMapOptionsUpdate(@NonNull MapOptions options) {
        if (mGoogleMap != null) {
            if (options.shouldDeleteMarkers()) {
                mGoogleMap.clear();
            } else {
                LatLng coords = new LatLng(options.getCoordinatesLatitude(), options.getCoordinatesLongitude());
                CameraPosition campos = new CameraPosition.Builder()
                        .target(coords)
                        .zoom(options.getCameraZoomLevel())
                        .build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(campos));
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(coords));
            }
        }
    }

    private void handleStatisticsUpdate(@NonNull TripStatistics statistics) {
        tvDistance.setText(statistics.getDistance());
        tvSpeed.setText(statistics.getSpeed());
        if (mStatisticsIdlingResource != null) {
            mStatisticsIdlingResource.onItemEmitted();
        }
    }

    private void goToStatisticsScreen() {
        NavHostFragment.findNavController(this).navigate(R.id.action_tripFragment_to_tripsListFragment);
    }

    private void goToSummaryScreen(long tripStartDate) {
        ActionTripFragmentToHistoryFragment action = actionTripFragmentToHistoryFragment();
        action.setTripStartDate(tripStartDate);
        NavHostFragment.findNavController(this).navigate(action);
    }

    private void logout() {
        NavHostFragment.findNavController(this).navigate(R.id.action_tripFragment_to_launchFragment);
    }

    private void showSnackbarMessage(@StringRes int stringId) {
        Snackbar.make(mRootView, stringId, Snackbar.LENGTH_LONG).show();
    }
}
