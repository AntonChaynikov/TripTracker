package com.antonchaynikov.triptracker.mainscreen;

import android.Manifest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.location.LocationFilter;
import com.antonchaynikov.triptracker.data.location.LocationSource;
import com.antonchaynikov.triptracker.data.location.PlatformLocationService;

import com.antonchaynikov.triptracker.data.location.ServiceManager;
import com.antonchaynikov.triptracker.data.repository.FireStoreDB;
import com.antonchaynikov.triptracker.data.tripmanager.StatisticsCalculator;
import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.antonchaynikov.triptracker.mainscreen.uistate.MapActivityUiState;
import com.antonchaynikov.triptracker.viewmodel.ViewModelActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.disposables.CompositeDisposable;

public class TripActivity extends ViewModelActivity implements View.OnClickListener, OnMapReadyCallback {

    private final static int ACCESS_FINE_LOCATION_REQUEST_CODE = 1;

    private final static String EXTRA_USER = "com.antonchaynikov.triptracker.TripActivity.user";
    private final static String TAG = TripActivity.class.getSimpleName();

    private boolean mPermissionGranted;
    private TripViewModel mViewModel;

    private View mRootView;
    private Button mButton;
    private GoogleMap mGoogleMap;

    private CompositeDisposable mSubscriptions;

    public static Intent getStartIntent(Context context, FirebaseUser user) {
        return new Intent(context, TripActivity.class)
                .putExtra(EXTRA_USER, user);
    }

    public static PendingIntent getNotificationContentIntent(@NonNull Context context) {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mPermissionGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

        mRootView = findViewById(R.id.vg_map_activity_layout);
        mButton = findViewById(R.id.btn_main_activity_location);
        mButton.setOnClickListener(this);

        addMapFragment();
        mSubscriptions = new CompositeDisposable();

        initViewModel();
    }

    private void initViewModel() {
        LocationSource locationSource = LocationSource.getInstance(new LocationFilter());
        ServiceManager serviceManager = ServiceManager.getInstance(this, locationSource, PlatformLocationService.class);
        mViewModel = new TripViewModel(
                TripManager.getInstance(
                        FireStoreDB.getInstance(),
                        locationSource,
                        new StatisticsCalculator()),
                serviceManager,
                mPermissionGranted);
        mSubscriptions.add(mViewModel.getAskLocationPermissionEventObserver().subscribe(event -> onLocationPermissionRequested()));
        mSubscriptions.add(mViewModel.getUiStateChangeEventObservable().subscribe(this::onUiStateUpdate));
        mSubscriptions.add(mViewModel.getShowSnackbarMessageBroadcast().subscribe(this::showSnackbarMessage));
        mSubscriptions.add(mViewModel.getMapOptionsObservable().subscribe(this::handleMapOptionsUpdate));
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
        mViewModel.onActionButtonClicked();
    }

    private void addMapFragment() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_activity_map_frame, mapFragment)
                .commit();
    }

    private void onLocationPermissionRequested() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, ACCESS_FINE_LOCATION_REQUEST_CODE);
    }

    private void onUiStateUpdate(@NonNull MapActivityUiState state) {
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

    private void showSnackbarMessage(@StringRes int stringId) {
        Snackbar.make(mRootView, stringId, Snackbar.LENGTH_LONG).show();
    }
}
