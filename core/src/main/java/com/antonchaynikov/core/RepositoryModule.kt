package com.antonchaynikov.core

import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.core.data.repository.firestore.FireStoreDB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class RepositoryModule {
    @Singleton
    @Provides
    fun repository(): Repository = FireStoreDB.getInstance()
}