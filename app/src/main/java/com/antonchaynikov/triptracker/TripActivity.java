package com.antonchaynikov.triptracker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.antonchaynikov.core.viewmodel.ViewModelActivity;
import com.google.android.material.navigation.NavigationView;

public class TripActivity extends ViewModelActivity {

    private boolean shouldShowActionBarItems;
    private AppBarConfiguration appBarConfiguration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        Toolbar toolbar = findViewById(R.id.trip_toolbar);
        setSupportActionBar(toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener(getOnDestinationChangedListener());

        NavigationView navView = findViewById(R.id.layout_nav_view);
        NavigationUI.setupWithNavController(navView, navController);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setDrawerLayout(drawerLayout)
                .build();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (shouldShowActionBarItems) {
            inflater.inflate(R.menu.menu_trip_activity, menu);
            return true;
        }
        return false;
    }
*/
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration);
    }

    private NavController.OnDestinationChangedListener getOnDestinationChangedListener() {
        /*
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
        */
        return (controller, destination, arguments) -> {};
    }
}
