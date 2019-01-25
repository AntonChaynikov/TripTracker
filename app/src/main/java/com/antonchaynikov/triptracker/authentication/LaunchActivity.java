package com.antonchaynikov.triptracker.authentication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import io.fabric.sdk.android.Fabric;
import com.crashlytics.android.Crashlytics;
import com.antonchaynikov.triptracker.R;

public class LaunchActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.frame_layout);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame, LaunchFragment.getFragment())
                .commit();
    }


}
