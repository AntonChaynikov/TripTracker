package com.antonchaynikov.triptracker

import androidx.test.espresso.IdlingRegistry

class AndroidTestUtils {
    companion object {
        @JvmStatic
        fun unregisterIdlingResource(resourceName : String) {
            val idlingRegistry = IdlingRegistry.getInstance()
            val resource = idlingRegistry.resources.find { resource -> resource.name == resourceName }
            if (resource != null) {
                idlingRegistry.unregister(resource)
            }
        }
    }
}