package com.antonchaynikov.triptracker.mainscreen;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.LocationService;
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

public class MapActivity extends ViewModelActivity implements View.OnClickListener, OnMapReadyCallback {

    private final static int ACCESS_FINE_LOCATION_REQUEST_CODE = 1;

    private final static String EXTRA_USER = "com.antonchaynikov.triptracker.MapActivity.user";
    private final static String TAG = MapActivity.class.getSimpleName();

    private boolean mPermissionGranted;
    private MapActivityViewModel mViewModel;

    private View mRootView;
    private Button mButton;
    private GoogleMap mGoogleMap;

    private CompositeDisposable mSubscriptions;

    public static Intent getStartIntent(Context context, FirebaseUser user) {
        return new Intent(context, MapActivity.class)
                .putExtra(EXTRA_USER, user);
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

        startLocationService();

        initViewModel();
    }

    private void initViewModel() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.dispose();

        if (isServiceRunning(LocationService.class) && mViewModel.isTripStopped()) {
            stopService(new Intent(this, LocationService.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            mPermissionGranted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    @Override
    public void onClick(View view) {

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

    private void onNewLocationReceived(@NonNull LatLng coordinates) {
        Log.d("Activity", "Location accounted");
        if (mGoogleMap != null) {
            CameraPosition campos = new CameraPosition.Builder()
                    .target(coordinates)
                    .zoom(20)
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(campos));
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(coordinates));
        }
    }

    private void showSnackbarMessage(@StringRes int stringId) {
        Snackbar.make(mRootView, stringId, Snackbar.LENGTH_LONG).show();
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startLocationService() {
        Intent service = new Intent(this, LocationService.class);
        if (!isServiceRunning(LocationService.class)) {
            ActivityCompat.startForegroundService(this, service);
        }

    }

}
