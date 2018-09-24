package com.antonchaynikov.triptracker.MapActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MapActivityViewModelTest {

    private MapActivityViewModel mTestSubject;

    @Spy
    private Consumer c;

    private boolean mIsLocationPermissionGrantedInitially = false;

    @Before
    public void setUp() {
        mTestSubject = new MapActivityViewModel(mIsLocationPermissionGrantedInitially);
        c = o -> {};
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
    public void shouldRequestPermissionIfNotAlreadyGranted() throws Exception{
        Consumer c = o -> {};
        mTestSubject.getLocationPermissionRequestEvent().subscribe(c);
        Mockito.verify(c, Mockito.times(1)).accept(Matchers.anyObject());
    }


}