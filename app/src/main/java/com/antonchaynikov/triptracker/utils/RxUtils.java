package com.antonchaynikov.triptracker.utils;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public final class RxUtils {

    private RxUtils() {}

    public static <T> ObservableTransformer<T, T> executeInBackgroundObserveOnMainThread() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

}
