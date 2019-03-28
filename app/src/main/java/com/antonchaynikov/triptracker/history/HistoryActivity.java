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
import androidx.fragment.app.Fragment;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import io.reactivex.disposables.CompositeDisposable;

public class HistoryActivity extends ViewModelActivity {

    private static final String EXTRA_TRIP_START_DATE = "com.antonchaynikov.triptracker.history.EXTRA_TRIP_START_DATE";

    public static Intent getStartIntent(@NonNull Context context, long tripStartDate) {
        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra(EXTRA_TRIP_START_DATE, tripStartDate);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_activity);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_activity_frame);
        if (!(fragment instanceof HistoryFragment)) {
            fragment = new HistoryFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_activity_frame, fragment)
                    .commit();
        }
    }
}
