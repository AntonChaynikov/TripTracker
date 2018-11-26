package com.antonchaynikov.triptracker.mainscreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.antonchaynikov.triptracker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private final static int ACCESS_FINE_LOCATION_REQUEST_CODE = 1;

    private final static String EXTRA_USER = "com.antonchaynikov.triptracker.MapActivity.user";

    private final static String TAG = "MainActivity";

    private boolean mPermissionGranted;

    private MapActivityViewModel mViewModel;

    private TextView mLocationTextView;
    private Button mButton;

    private CompositeDisposable mSubscriptions;

    public static Intent getStartIntent(Context context, FirebaseUser user) {
        return new Intent(context, MainActivity.class)
                .putExtra(EXTRA_USER, user);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mPermissionGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

        mLocationTextView = findViewById(R.id.main_activity_textView);
        mButton = findViewById(R.id.main_activity_button);
        mButton.setOnClickListener(this);

        addMapFragment();

        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSubscriptions.dispose();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            mPermissionGranted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
        Log.d(TAG, "Permission Granted = " + mPermissionGranted);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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

}
