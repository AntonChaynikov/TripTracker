package com.antonchaynikov.core.authentication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthModule {

    @Singleton
    @Provides
    public Auth provideAuth() {
        return TripAuth.INSTANCE;
    }

}
