package com.antonchaynikov.triptracker.application

import android.content.Context
import com.antonchaynikov.core.RepositoryModule
import com.antonchaynikov.core.TripManagerModule
import com.antonchaynikov.core.authentication.AuthModule
import com.antonchaynikov.core.viewmodel.CommonViewModelModule
import dagger.BindsInstance
import dagger.Component

@Component(modules = [
    TripManagerModule::class,
    RepositoryModule::class,
    CommonViewModelModule::class,
    AuthModule::class
])
interface NewAppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): NewAppComponent
    }
}