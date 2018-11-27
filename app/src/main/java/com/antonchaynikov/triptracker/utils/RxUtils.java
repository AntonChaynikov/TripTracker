package com.antonchaynikov.triptracker.utils;

import com.antonchaynikov.triptracker.R;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtils {

    public static <T> ObservableTransformer<T, T> executeInBackgroundObserveOnMainThread() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

}
