package com.antonchaynikov.triptracker.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.fabric.sdk.android.Fabric;

import com.antonchaynikov.triptracker.mainscreen.TripActivity;
import com.crashlytics.android.Crashlytics;
import com.antonchaynikov.triptracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LaunchActivity extends AppCompatActivity {

    public static Intent getStartIntent(@NonNull Context context) {
        return new Intent(context, LaunchActivity.class);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.frame_layout);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(TripActivity.getStartIntent(this, user));
            finish();
        }
        else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame, LaunchFragment.getFragment())
                    .commit();
        }
    }


}
