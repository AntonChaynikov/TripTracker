package com.antonchaynikov.core.injection

import android.util.Log
import androidx.fragment.app.Fragment

object Injector {

    @JvmStatic
    lateinit var injector: IInjector

    @JvmStatic
    fun init(iInjector: IInjector) {
        Log.d(Injector::class.java.canonicalName, "init $iInjector")
        injector = iInjector
    }

    @JvmStatic
    fun inject(fragment: Fragment) {
        injector.inject(fragment)
    }
}