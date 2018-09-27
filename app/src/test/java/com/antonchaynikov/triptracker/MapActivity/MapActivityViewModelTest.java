package com.antonchaynikov.triptracker.MapActivity;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
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
    private Consumer rawConsumer;
    @Spy
    private Consumer<LatLng> latLngConsumer;
    private LatLng testLocation = new LatLng(1,1);
    @Mock
    private LocationSource mockLocationSource;

    private boolean mIsLocationPermissionGrantedInitially = false;

    @Before
    public void setUp() throws Exception {
        mTestSubject = new MapActivityViewModel(mockLocationSource, mIsLocationPermissionGrantedInitially);
        Mockito.doNothing().when(rawConsumer).accept(ArgumentMatchers.any());
        Mockito.doNothing().when(latLngConsumer).accept(ArgumentMatchers.any());
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

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRequestPermissionIfNotAlreadyGranted() throws Exception{
        mTestSubject.getLocationPermissionRequestEvent().subscribe(rawConsumer);
        Mockito.verify(rawConsumer, Mockito.times(1)).accept(ArgumentMatchers.any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBroadcastLocationsWhetUpdatesToggledOn() throws Exception {
        mTestSubject.getNewLocationReceivedEvent().subscribe(latLngConsumer);
        mTestSubject.onLocationPermissionGranted();
        mTestSubject.toggleCoordinatesUpdate();
        Mockito.verify(rawConsumer, Mockito.times(1)).accept(testLocation);
    }




}