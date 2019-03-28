package com.antonchaynikov.triptracker.mainscreen;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.viewmodel.ViewModelActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class TripActivity extends ViewModelActivity {

    private static final String TAG = TripActivity.class.getSimpleName();

    public static Intent getStartIntent(Context context) {
        return new Intent(context, TripActivity.class);
    }

    public static PendingIntent getNotificationContentIntent(@NonNull Context context) {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_activity);
        Toolbar toolbar = findViewById(R.id.trip_toolbar);
        setSupportActionBar(toolbar);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_activity_frame);
        if (fragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_activity_frame, new TripFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trip_activity, menu);
        return true;
    }
}
