package com.antonchaynikov.triptracker.data.location;

import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;
import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LocationServiceTest {

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();
    @Mock
    private LocationProvider mockLocationProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCallToStopUpdates_whenOnDestroy() throws Exception {

        IBinder binder = mServiceRule.bindService(new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), LocationService.class));
        LocationService service = ((LocationService.LocationServiceBinder) binder).getLocationService();
        service.setLocationProvider(mockLocationProvider);

        service.startUpdates();
        service.onDestroy();

        verify(mockLocationProvider).stopUpdates(any(LocationConsumer.class));
    }

    @Test
    public void shouldEmitGeolocationError_whenNoLocationProvider() throws Exception {

        TestObserver<Boolean> geoErrorObserver = TestObserver.create();
        IBinder binder = mServiceRule.bindService(new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), LocationService.class));
        LocationService service = ((LocationService.LocationServiceBinder) binder).getLocationService();
        service.getGeolocationAvailabilityUpdatesObservable().subscribe(geoErrorObserver);

        service.startUpdates();

        geoErrorObserver.assertValue(false);
    }

    @Test
    public void shouldEmitLocation_whenOneReceived() throws Exception {

        TestObserver<Location> locationsObserver = TestObserver.create();
        IBinder binder = mServiceRule.bindService(new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), LocationService.class));
        LocationService service = ((LocationService.LocationServiceBinder) binder).getLocationService();
        service.setLocationProvider(mockLocationProvider);
        service.getLocationsStream().subscribe(locationsObserver);
        ArgumentCaptor<LocationConsumer> argumentCaptor = ArgumentCaptor.forClass(LocationConsumer.class);
        doNothing().when(mockLocationProvider).startUpdates(argumentCaptor.capture());

        service.startUpdates();
        Location locToEmit = new Location("mockSource");
        argumentCaptor.getValue().onNewLocationUpdate(locToEmit);

        locationsObserver.assertValue(locToEmit);
    }

    @Test
    public void shouldEmitGeolocationError_whenOneReceived() throws Exception {

        TestObserver<Boolean> geoErrorObserver = TestObserver.create();
        IBinder binder = mServiceRule.bindService(new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), LocationService.class));
        LocationService service = ((LocationService.LocationServiceBinder) binder).getLocationService();
        service.setLocationProvider(mockLocationProvider);
        service.getGeolocationAvailabilityUpdatesObservable().subscribe(geoErrorObserver);
        ArgumentCaptor<LocationConsumer> argumentCaptor = ArgumentCaptor.forClass(LocationConsumer.class);
        doNothing().when(mockLocationProvider).startUpdates(argumentCaptor.capture());

        service.startUpdates();
        argumentCaptor.getValue().onLocationUpdatesAvailabilityChange(false);

        geoErrorObserver.assertValue(false);
    }
}