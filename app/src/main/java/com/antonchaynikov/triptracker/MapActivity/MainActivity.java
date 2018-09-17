package com.antonchaynikov.triptracker.MapActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.antonchaynikov.triptracker.Injector;
import com.antonchaynikov.triptracker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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

        LocationSource locationSource = Injector.injectLocationSource(this);

        mViewModel = new MapActivityViewModel();

        mLocationTextView = findViewById(R.id.main_activity_textView);
        mButton = findViewById(R.id.main_activity_button);
        mButton.setOnClickListener(this);

        addMapFragment();

        mSubscriptions = new CompositeDisposable();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSubscriptions.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        //mSubscriptions.add(subscribeToEditTextChangeEvent(mViewModel.getEditTextChangeEvent()));
        //mSubscriptions.add(subscribeToButtonTextChangeEvent(mViewModel.getButtonTextChangeEvent()));
        //mSubscriptions.add(subscribeToPermissionRequestEvent(mViewModel.getAskPermissionEvent()));
        FirebaseUser user = getIntent().getParcelableExtra(EXTRA_USER);
        Toast.makeText(this, user.getDisplayName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            mPermissionGranted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
       // mViewModel.onPermissionRequestResult(mPermissionGranted);
        Log.d(TAG, "Permission Granted = " + mPermissionGranted);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //mViewModel.onMapReady(googleMap);
    }

    @Override
    public void onClick(View view) {
        mViewModel.toggleCoordinatesUpdate();
    }

    private Disposable subscribeToEditTextChangeEvent(Observable<String> event) {
        return event.subscribe(text -> mLocationTextView.setText(text));
    }

    private Disposable subscribeToButtonTextChangeEvent(Observable<Boolean> event) {
        return event.subscribe(aBoolean -> {
            if (aBoolean) {
                mButton.setText(R.string.button_act);
            } else {
                mButton.setText(R.string.button_stop);
            }
        });
    }

    private Disposable subscribeToPermissionRequestEvent(Observable<Boolean> event) {
        return event.subscribe(aBoolean -> {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MainActivity.this, permissions, ACCESS_FINE_LOCATION_REQUEST_CODE);
        });
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
