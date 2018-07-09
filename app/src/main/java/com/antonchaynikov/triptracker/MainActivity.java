package com.antonchaynikov.triptracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private final static int MAX_LOCATIONS_NUM_STORED = 6;
    private final static long LOCATION_IRRELEVANT_AFTER = 1000 * 20;
    private final static int ACCESS_FINE_LOCATION_REQUEST_CODE = 1;

    private final static String TAG = "MainActivity";

    private boolean mPermissionGranted;

    private LocationSource mLocationSource;
    private TextView mLocationTextView;
    private GoogleMap mGoogleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mPermissionGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
        if (!mPermissionGranted) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, ACCESS_FINE_LOCATION_REQUEST_CODE);
        }
        mLocationSource = PlatformLocationSource.getInstance(
                this,
                new PreciseLocationUpdatePolicy(LOCATION_IRRELEVANT_AFTER, MAX_LOCATIONS_NUM_STORED)
        );
        mLocationTextView = findViewById(R.id.main_activity_textView);
        findViewById(R.id.main_activity_button).setOnClickListener(this);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_activity_map_frame, mapFragment)
                .commit();

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");
        if (mPermissionGranted) {
            mLocationSource.toggleLocationUpdates();
            if (mLocationSource.isUpdateEnabled()) {
                mLocationSource.getLocation()
                        .doOnNext(new Consumer<Location>() {
                            @Override
                            public void accept(Location location) throws Exception {
                                mLocationTextView.setText(location.toString());
                                mGoogleMap.clear();
                                mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .title(getString(R.string.app_name))
                                );
                            }
                        })
                        .subscribe();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPermissionGranted = true;
        }
        Log.d(TAG, "Permission Granted = " + mPermissionGranted);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

}
