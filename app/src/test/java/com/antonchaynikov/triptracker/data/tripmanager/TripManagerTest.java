package com.antonchaynikov.triptracker.data.tripmanager;

import android.location.Location;

import com.antonchaynikov.triptracker.data.location.LocationSource;
import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.repository.Repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class TripManagerTest {

    private TripManager mTestSubject;
    @Mock
    private Repository mockRepository;
    @Mock
    private LocationSource mockLocationSource;
    @Mock
    private Location mockLocation;
    @Mock
    private StatisticsCalculator mockStatisticsCalculator;

    private CompletableSubject startTripCompletable;
    private CompletableSubject finishTripCompletable;
    private PublishSubject<Location> coordinateObservable;

    @Before
    public void setUp() throws Exception {
        startTripCompletable = CompletableSubject.create();
        finishTripCompletable = CompletableSubject.create();
        coordinateObservable = PublishSubject.create();

        MockitoAnnotations.initMocks(this);

        doReturn(CompletableSubject.complete()).when(mockRepository).addTrip(any(Trip.class));
        doReturn(CompletableSubject.complete()).when(mockRepository).updateTrip(any(Trip.class));
        doReturn(coordinateObservable).when(mockLocationSource).getLocationsObservable();
        doNothing().when(mockStatisticsCalculator).addCoordinate(any(Location.class));

        mTestSubject = TripManager.getInstance(mockRepository, mockLocationSource, mockStatisticsCalculator);
    }

    @After
    public void tearDown() {
        TripManager.resetInstance();
    }

    @Test
    public void startTrip_shouldCallRepository_withCurrentTime() {
        long timeBeforeStart = System.currentTimeMillis();
        Disposable d = mTestSubject.startTrip().subscribe();
        long timeAfterStart = System.currentTimeMillis();

        long timeOfStart = mTestSubject.getCurrentTrip().getStartDate();

        assertTrue(timeBeforeStart <= timeOfStart && timeAfterStart >= timeOfStart);
    }

    @Test
    public void finishTrip_shouldCallRepository_withCurrentTime_withCorrectTrip() {
        ArgumentCaptor<Trip> argumentCaptor = ArgumentCaptor.forClass(Trip.class);
        Disposable sd = mTestSubject.startTrip().subscribe();

        long timeBeforeFinish = System.currentTimeMillis();
        Disposable fd = mTestSubject.finishTrip().subscribe();
        long timeAfterFinish = System.currentTimeMillis();

        verify(mockRepository).updateTrip(argumentCaptor.capture());

        long timeOfFinish = argumentCaptor.getValue().getEndDate();

        assertTrue(timeBeforeFinish <= timeOfFinish && timeAfterFinish >= timeOfFinish);
    }

    @Test
    public void startTrip_whenTripStarted_shouldInformLocationSourceForUpdates() {
        Disposable sd = mTestSubject.startTrip().subscribe();

        verify(mockLocationSource).startUpdates();
    }

    @Test
    public void finishTrip_whenTripFinished_shouldCallLocationSourceToStopUpdating() {
        Disposable sd = mTestSubject.startTrip().subscribe();
        Disposable fd = mTestSubject.finishTrip().subscribe();

        verify(mockLocationSource).finishUpdates();
    }

    @Test
    public void shouldBroadcastStatistics_whenNewCoordinateReceived() {
        TestObserver<TripCoordinate> tripCoordinateObserver = TestObserver.create();
        TestObserver<Trip> tripUpdateObserver = TestObserver.create();
        mTestSubject.getTripUpdatesStream().subscribe(tripUpdateObserver);
        mTestSubject.getCoordinatesStream().subscribe(tripCoordinateObserver);
        Disposable sd = mTestSubject.startTrip().subscribe();

        doReturn(2L).when(mockLocation).getTime();
        doReturn(3D).when(mockLocation).getLatitude();
        doReturn(4D).when(mockLocation).getLongitude();

        coordinateObservable.onNext(mockLocation);

        assertEquals(1, tripCoordinateObserver.valueCount());
        assertEquals(1, tripUpdateObserver.valueCount());

        assertEquals(new TripCoordinate(2, 3, 4), tripCoordinateObserver.values().get(0));
    }

    @Test
    public void shouldUpdateTripStatistics_whenNewCoordinateReceived() {
        doReturn(12d).when(mockStatisticsCalculator).getDistance();
        doReturn(21d).when(mockStatisticsCalculator).getSpeed();

        mTestSubject.resetInstance();
        mTestSubject = TripManager.getInstance(mockRepository, mockLocationSource, mockStatisticsCalculator);

        TestObserver<Trip> tripUpdateObserver = TestObserver.create();
        mTestSubject.getTripUpdatesStream().subscribe(tripUpdateObserver);

        Disposable sd = mTestSubject.startTrip().subscribe();

        coordinateObservable.onNext(mockLocation);

        Trip tripUpdate = tripUpdateObserver.values().get(0);

        assertEquals(12d, tripUpdate.getDistance(), 0.001);
        assertEquals(21d, tripUpdate.getSpeed(), 0.001);
    }
}
