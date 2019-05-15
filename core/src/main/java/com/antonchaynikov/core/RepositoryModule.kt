package com.antonchaynikov.core

import com.antonchaynikov.core.data.repository.Repository
import com.antonchaynikov.core.data.repository.firestore.FireStoreDB
import dagger.Module
import dagger.Provides

@Module
open class RepositoryModule {
    @Provides
    fun repository(): Repository = FireStoreDB.getInstance()
}