package com.antonchaynikov.triptracker.mainscreen;

import android.Manifest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.location.LocationFilter;
import com.antonchaynikov.triptracker.data.location.LocationSource;
import com.antonchaynikov.triptracker.data.location.PlatformLocationService;

import com.antonchaynikov.triptracker.data.location.ServiceManager;
import com.antonchaynikov.triptracker.data.repository.FireStoreDB;
import com.antonchaynikov.triptracker.data.tripmanager.StatisticsCalculator;
import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.antonchaynikov.triptracker.mainscreen.uistate.MapActivityUiState;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.disposables.CompositeDisposable;

public class TripActivity extends ViewModelActivity implements View.OnClickListener, OnMapReadyCallback {

    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1;

    private static final String EXTRA_USER = "com.antonchaynikov.triptracker.TripActivity.user";
    private static final String TAG = TripActivity.class.getSimpleName();

    private boolean mPermissionGranted;
    private TripViewModel mViewModel;

    private View mRootView;
    private Button mButton;
    private GoogleMap mGoogleMap;
    private TextView tvDistance;
    private TextView tvSpeed;

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
        Toolbar toolbar = findViewById(R.id.trip_toolbar);
        setSupportActionBar(toolbar);

        tvDistance = findViewById(R.id.tv_statistics_distance);
        tvSpeed = findViewById(R.id.tv_statistics_speed);

        mPermissionGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

        mRootView = findViewById(R.id.vg_trip_activity_map_frame);
        mButton = findViewById(R.id.btn_trip_activity_location);
        mButton.setOnClickListener(this);

        addMapFragment();
        mSubscriptions = new CompositeDisposable();

        initViewModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trip_activity, menu);
        return true;
    }

    private void initViewModel() {
        LocationSource locationSource = LocationSource.getInstance(new LocationFilter());
        ServiceManager serviceManager = ServiceManager.getInstance(this, locationSource, PlatformLocationService.class);
        ViewModelFactory factory = new ViewModelFactory() {
            @Override
            public <T extends BasicViewModel> T create(@NonNull Class<T> clazz) {
                return (T) new TripViewModel(
                        TripManager.getInstance(
                                FireStoreDB.getInstance(),
                                locationSource,
                                new StatisticsCalculator()),
                        serviceManager,
                        mPermissionGranted);
            }
        };
        mViewModel = ViewModelProviders.of(this, factory).get(TripViewModel.class);
        mSubscriptions.add(mViewModel.getAskLocationPermissionEventObserver().subscribe(event -> onLocationPermissionRequested()));
        mSubscriptions.add(mViewModel.getUiStateChangeEventObservable().subscribe(this::onUiStateUpdate));
        mSubscriptions.add(mViewModel.getShowSnackbarMessageBroadcast().subscribe(this::showSnackbarMessage));
        mSubscriptions.add(mViewModel.getMapOptionsObservable().subscribe(this::handleMapOptionsUpdate));
        mSubscriptions.add(mViewModel.getTripStatisticsStreamObservable().subscribe(this::handleStatisticsUpdate));
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
                .add(R.id.vg_trip_activity_map_frame, mapFragment)
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

    private void handleStatisticsUpdate(@NonNull TripStatistics statistics) {
        tvDistance.setText(getString(R.string.statistics_distance, statistics.getDistance()));
        tvSpeed.setText(getString(R.string.statistics_speed, statistics.getSpeed()));
    }

    private void showSnackbarMessage(@StringRes int stringId) {
        Snackbar.make(mRootView, stringId, Snackbar.LENGTH_LONG).show();
    }
}
