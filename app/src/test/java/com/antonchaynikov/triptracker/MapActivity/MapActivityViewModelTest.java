package com.antonchaynikov.triptracker.MapActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static org.junit.Assert.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class MapActivityViewModelTest {

    private MapActivityViewModel mTestSubject;

    private boolean mIsLocationPermissionGrantedInitially = false;

    @Before
    public void setUp() {
        mTestSubject = new MapActivityViewModel(mIsLocationPermissionGrantedInitially);
    }

    @Test
    public void whenLocationUpdatesToggledCorrectStatusShouldBeBroadcasted() {
        int expectedUpdateStatus = MapActivityViewModel.STATUS_IDLE;
        mTestSubject.onLocationPermissionGranted();
        // status == idle should be received
        Disposable subscription = mTestSubject
                .getLocationUpdateStatusChangeEvent()
                .subscribe(getStatusConsumer(expectedUpdateStatus));
        subscription.dispose();

        // status == updating should be received
        expectedUpdateStatus = MapActivityViewModel.STATUS_UPDATING;
        mTestSubject.toggleCoordinatesUpdate();
        subscription = mTestSubject
                .getLocationUpdateStatusChangeEvent()
                .subscribe(getStatusConsumer(expectedUpdateStatus));
        subscription.dispose();

        // status == updating should still be received
        subscription = mTestSubject
                .getLocationUpdateStatusChangeEvent()
                .subscribe(getStatusConsumer(expectedUpdateStatus));

    }

    private Consumer<Integer> getStatusConsumer(int expectedUpdateStatus) {
        return actualStatus -> {
                if (actualStatus == null) {
                    throw new AssertionError("Status shouldn't be null");
                }
                assertEquals(expectedUpdateStatus, (int) actualStatus);
            };
    }

    @Test
    public void shouldRequestPermissionIfNotAlreadyGranted() {
        boolean permissionRequested = false;
        mTestSubject.getLocationPermissionRequestEvent().subscribe(o -> {
            return;
        });
        throw new AssertionError("Location permission should have been requested");
    }


}