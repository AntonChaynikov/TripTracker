package com.antonchaynikov.triptracker.injection;

import android.content.Context;

import com.antonchaynikov.triptracker.data.repository.Repository;
import com.antonchaynikov.triptracker.data.repository.firestore.FireStoreDB;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Context mContext;

    public AppModule(Context context) {
        mContext = context.getApplicationContext();
    }

    @Singleton
    @Provides
    public Context provideApplicationContext() {
        return mContext;
    }

    @Singleton
    @Provides
    public Repository provideRepository() {
        return FireStoreDB.getInstance();
    }

}
