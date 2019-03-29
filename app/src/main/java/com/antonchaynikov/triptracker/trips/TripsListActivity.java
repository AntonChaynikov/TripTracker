package com.antonchaynikov.triptracker.trips;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.application.TripApplication;
import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.repository.firestore.FireStoreDB;
import com.antonchaynikov.triptracker.history.HistoryFragment;
import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;
import com.antonchaynikov.triptracker.viewmodel.ViewModelActivity;
import com.antonchaynikov.triptracker.viewmodel.ViewModelFactory;
import com.antonchaynikov.triptracker.viewmodel.ViewModelProviders;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import io.reactivex.disposables.CompositeDisposable;

public class TripsListActivity extends ViewModelActivity {

    public static Intent getStartIntent(@NonNull Context context) {
        return new Intent(context, TripsListActivity.class);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_activity);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_activity_frame);
        if (!(fragment instanceof TripsListFragment)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_activity_frame, new TripsListFragment())
                    .commit();
        }
    }
}
