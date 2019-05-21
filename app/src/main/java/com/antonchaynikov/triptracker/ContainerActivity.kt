package com.antonchaynikov.triptracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class ContainerActivity: AppCompatActivity() {

    companion object {
        @JvmStatic
        fun getStartIntent(context: Context) = Intent(context, ContainerActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_activity)
    }

    fun attachFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
    }
}