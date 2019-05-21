package com.antonchaynikov.triptracker

import androidx.fragment.app.Fragment
import com.antonchaynikov.core.injection.IInjector
import com.antonchaynikov.triptracker.application.NewAppComponent
import com.antonchaynikov.triptracker.application.TripApplication

object MainInjector: IInjector {

    lateinit var appComponent: NewAppComponent

    fun init(application: TripApplication) {

    }

    override fun inject(fragment: Fragment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}