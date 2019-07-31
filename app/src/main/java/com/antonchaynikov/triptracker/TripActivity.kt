package com.antonchaynikov.triptracker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class TripActivity : AppCompatActivity() {

    private val tag = TripActivity::class.java.canonicalName

    private val rootDestinations = setOf(R.id.dest_auth, R.id.dest_trip, R.id.dest_statistics, R.id.dest_about)

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration.Builder(rootDestinations)
                .build()

        setSupportActionBar(findViewById(R.id.trip_toolbar))
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView = findViewById(R.id.trip_bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener(getOnDestinationChangedListener())
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getOnDestinationChangedListener(): (NavController, NavDestination, Bundle?) -> Unit =
            { controller, destination, args ->

                if (destination.id == R.id.dest_auth) {
                    bottomNavigationView.visibility = View.INVISIBLE
                } else {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
}
