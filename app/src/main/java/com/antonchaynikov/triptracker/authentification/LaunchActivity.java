package com.antonchaynikov.triptracker.authentification;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.antonchaynikov.triptracker.R;

public class LaunchActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame, LaunchFragment.getFragment())
                .commit();
    }


}
