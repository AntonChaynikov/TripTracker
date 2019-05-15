package com.antonchaynikov.triptracker.injection;

import android.content.Context;

import com.antonchaynikov.triptracker.data.repository.Repository;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    Context appContext();

    Repository repository();

}
