package com.antonchaynikov.core.injection

import androidx.fragment.app.Fragment

interface IInjector {
    fun inject(fragment: Fragment)
}