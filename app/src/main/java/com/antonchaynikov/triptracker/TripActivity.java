package com.antonchaynikov.triptracker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.antonchaynikov.core.viewmodel.ViewModelActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class TripActivity extends ViewModelActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    private boolean shouldShowActionBarItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_activity);
        Toolbar toolbar = findViewById(R.id.trip_toolbar);
        setSupportActionBar(toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener(getOnDestinationChangedListener());
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (shouldShowActionBarItems) {
            inflater.inflate(R.menu.menu_trip_activity, menu);
            return true;
        }
        return false;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, new AppBarConfiguration.Builder(navController.getGraph()).build());
    }

    private NavController.OnDestinationChangedListener getOnDestinationChangedListener() {
        return (controller, destination, arguments) -> {
            shouldShowActionBarItems = destination.getId() == R.id.tripFragment;
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                if (destination.getId() == R.id.launchFragment) {
                    actionBar.hide();
                } else {
                    actionBar.show();
                    invalidateOptionsMenu();
                }
            }
        };
    }
}
