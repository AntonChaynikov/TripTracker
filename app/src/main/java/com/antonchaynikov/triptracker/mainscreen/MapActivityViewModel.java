package com.antonchaynikov.triptracker.mainscreen;

import com.antonchaynikov.triptracker.viewmodel.BasicViewModel;

import io.reactivex.disposables.CompositeDisposable;

class MapActivityViewModel extends BasicViewModel {

    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    boolean isTripStopped() {
        return false;
    }
}


