package com.antonchaynikov.triptracker.application

import androidx.fragment.app.Fragment
import com.antonchaynikov.core.injection.IInjector
import dagger.android.support.AndroidSupportInjection

object DaggerAndroidInjector: IInjector {
    override fun inject(fragment: Fragment) {
        AndroidSupportInjection.inject(fragment)
    }
}