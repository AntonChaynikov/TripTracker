package com.antonchaynikov.core.authentication;

import com.google.firebase.auth.FirebaseAuth;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthModule {

    @Provides
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

}
