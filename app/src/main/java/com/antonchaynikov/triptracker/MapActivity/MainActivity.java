package com.antonchaynikov.triptracker.MapActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.antonchaynikov.triptracker.AlarmListener;
import com.antonchaynikov.triptracker.Injector;
import com.antonchaynikov.triptracker.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private final static int MAX_LOCATIONS_NUM_STORED = 6;
    private final static long LOCATION_IRRELEVANT_AFTER = 1000 * 20;
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
        checkGooglePlayServicesAvailability();
        setContentView(R.layout.main_activity);
        mPermissionGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

        LocationSource locationSource = Injector.injectLocationSource(this,
                new PreciseLocationUpdatePolicy(LOCATION_IRRELEVANT_AFTER, MAX_LOCATIONS_NUM_STORED));

        mViewModel = new MapActivityViewModel(locationSource, new Mapper(new TrackCalculator()), mPermissionGranted);

        mLocationTextView = findViewById(R.id.main_activity_textView);
        mButton = findViewById(R.id.main_activity_button);
        mButton.setOnClickListener(this);
        addMapFragment();
        mSubscriptions = new CompositeDisposable();
        AlarmManager alarmManager = (AlarmManager) getSystemService(AppCompatActivity.ALARM_SERVICE);
        if (alarmManager != null) {
            PendingIntent intent = PendingIntent.getBroadcast(this, 0, AlarmListener.makeIntent(this), 0);
            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.ELAPSED_REALTIME_WAKEUP, TimeUnit.SECONDS.toMillis(15), intent);
            Toast.makeText(this, "Alarm set",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSubscriptions.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkGooglePlayServicesAvailability();
        mSubscriptions.add(subscribeToEditTextChangeEvent(mViewModel.getEditTextChangeEvent()));
        mSubscriptions.add(subscribeToButtonTextChangeEvent(mViewModel.getButtonTextChangeEvent()));
        mSubscriptions.add(subscribeToPermissionRequestEvent(mViewModel.getAskPermissionEvent()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            mPermissionGranted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
        mViewModel.onPermissionRequestResult(mPermissionGranted);
        Log.d(TAG, "Permission Granted = " + mPermissionGranted);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mViewModel.onMapReady(googleMap);
    }

    @Override
    public void onClick(View view) {
        mViewModel.onCoordinatesButtonClick();
    }

    private Disposable subscribeToEditTextChangeEvent(Observable<String> event) {
        return event.subscribe(text -> mLocationTextView.setText(text));
    }

    private Disposable subscribeToButtonTextChangeEvent(Observable<Boolean> event) {
        return event.subscribe(aBoolean -> {
            if (aBoolean == MapActivityViewModel.BUTTON_TEXT_START) {
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

    private void checkGooglePlayServicesAvailability() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int status = apiAvailability.isGooglePlayServicesAvailable(getApplicationContext());
        if (status != ConnectionResult.SUCCESS) {
            apiAvailability.makeGooglePlayServicesAvailable(this);
        }
    }
}
