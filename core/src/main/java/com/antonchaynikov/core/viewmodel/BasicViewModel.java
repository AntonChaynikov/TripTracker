package com.antonchaynikov.core.viewmodel;

import androidx.annotation.CallSuper;
import androidx.annotation.StringRes;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

public class BasicViewModel {

    protected PublishSubject<Boolean> mShowProgressBarEventBroadcast = PublishSubject.create();
    private PublishSubject<Integer> mShowSnackbarMessageBroadcast = PublishSubject.create();

    public Observable<Boolean> getShowProgressBarEventBroadcast() {
        return mShowProgressBarEventBroadcast.observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Integer> getShowSnackbarMessageBroadcast() {
        return mShowSnackbarMessageBroadcast.observeOn(AndroidSchedulers.mainThread());
    }

    protected void showSnackbarMessage(@StringRes int stringId) {
        mShowSnackbarMessageBroadcast.onNext(stringId);
    }

    @CallSuper
    protected void onCleared() {}
}
