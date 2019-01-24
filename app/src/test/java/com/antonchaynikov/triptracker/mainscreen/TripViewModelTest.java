package com.antonchaynikov.triptracker.mainscreen;

import com.antonchaynikov.triptracker.RxImmediateSchedulerRule;
import com.antonchaynikov.triptracker.data.location.ServiceManager;
import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.tripmanager.TripManager;
import com.antonchaynikov.triptracker.mainscreen.uistate.MapActivityUiState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.PublishSubject;

import static com.antonchaynikov.triptracker.mainscreen.uistate.MapActivityUiState.State.IDLE;
import static com.antonchaynikov.triptracker.mainscreen.uistate.MapActivityUiState.State.STARTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TripViewModelTest {

    @ClassRule
    public static final RxImmediateSchedulerRule schedulers = new RxImmediateSchedulerRule();

    private TripViewModel mTestSubject;
    @Mock
    private TripManager mockTripManager;
    @Mock
    private ServiceManager mockServiceManager;

    private PublishSubject<Trip> statsStream;
    private PublishSubject<TripCoordinate> coordsStream;

    @Before
    public void setUp() throws Exception {
        statsStream = PublishSubject.create();
        coordsStream = PublishSubject.create();

        doReturn(Completable.complete()).when(mockTripManager).startTrip();
        doReturn(Completable.complete()).when(mockTripManager).finishTrip();
        doReturn(statsStream).when(mockTripManager).getTripUpdatesStream();
        doReturn(coordsStream).when(mockTripManager).getCoordinatesStream();
        mTestSubject = new TripViewModel(mockTripManager, mockServiceManager, true);
    }

    @Test
    public void shouldBroadcastIdleUiState_whenJustCreated() {
        TestObserver<MapActivityUiState.State> uiStateTestObserver = TestObserver.create();
        mTestSubject.getUiStateChangeEventObservable().map(MapActivityUiState::getState).subscribe(uiStateTestObserver);

        uiStateTestObserver.assertValue(IDLE);
    }

    @Test
    public void shouldBroadcastRunningUIState_whenTripStarts() {
        TestObserver<MapActivityUiState.State> uiStateTestObserver = TestObserver.create();
        mTestSubject.getUiStateChangeEventObservable().map(MapActivityUiState::getState).subscribe(uiStateTestObserver);

        mTestSubject.onActionButtonClicked();

        // IDLE by default, then started
        uiStateTestObserver.assertValues(IDLE, STARTED);
    }

    @Test
    public void shouldBroadcastIdleUIState_whenTripStops() {
        TestObserver<MapActivityUiState.State> uiStateTestObserver = TestObserver.create();
        mTestSubject.getUiStateChangeEventObservable().map(MapActivityUiState::getState).subscribe(uiStateTestObserver);

        // Starts trip
        mTestSubject.onActionButtonClicked();
        // Ends trip
        mTestSubject.onActionButtonClicked();

        // IDLE by default, then started and stopped
        uiStateTestObserver.assertValues(IDLE, STARTED, IDLE);
    }

    @Test
    public void isTripStarted_shouldReturnTrue_ifTripStarted_falseOtherwise() {
        assertFalse(mTestSubject.isTripStarted());

        // Starts trip
        mTestSubject.onActionButtonClicked();
        assertTrue(mTestSubject.isTripStarted());

        // Ends trip
        mTestSubject.onActionButtonClicked();
        assertFalse(mTestSubject.isTripStarted());
    }

    @Test
    public void onActionButtonClicked_shouldAskForLocationPermission_ifNotGranted() {
        // The location permission hasn't been granted
        mTestSubject.onLocationPermissionUpdate(false);

        TestObserver<Boolean> permissionTestObserver = TestObserver.create();
        mTestSubject.getAskLocationPermissionEventObserver().subscribe(permissionTestObserver);

        mTestSubject.onActionButtonClicked();

        permissionTestObserver.assertValues(true);
    }

    @Test
    public void onActionButtonClicked_shouldNotAskForLocationPermission_ifAlreadyGranted() {
        TestObserver<Boolean> permissionTestObserver = TestObserver.create();
        mTestSubject.getAskLocationPermissionEventObserver().subscribe(permissionTestObserver);

        mTestSubject.onActionButtonClicked();

        permissionTestObserver.assertEmpty();
    }

    @Test
    public void onActionButtonClicked_shouldNotStartTrip_ifLocationPermissionNotGranted() {
        // The location permission hasn't been granted
        mTestSubject.onLocationPermissionUpdate(false);

        mTestSubject.onActionButtonClicked();

        assertFalse(mTestSubject.isTripStarted());
    }

    @Test
    public void onActionButtonClicked_whenIntendedToStartTrip_shouldCallTripManagerToStartTrip() {
        // Intending to start a trip
        mTestSubject.onActionButtonClicked();

        verify(mockTripManager).startTrip();
    }

    @Test
    public void onActionButtonClicked_whenFinishingTrip_shouldEraseMarkers() {
        TestObserver<MapOptions> mapOptionsTestObserver = TestObserver.create();
        mTestSubject.getMapOptionsObservable().subscribe(mapOptionsTestObserver);

        mTestSubject.onActionButtonClicked();
        mTestSubject.onActionButtonClicked();

        // 1 item should have been emitted
        assertEquals(1, mapOptionsTestObserver.valueCount());

        MapOptions mapOptions = mapOptionsTestObserver.values().get(0);
        assertTrue(mapOptions.shouldDeleteMarkers());
    }

    @Test
    public void onActionButtonClicked_whenStarting_shouldUpdateState_whenTripManagerRequestCompletes() {
        CompletableSubject tripManagerRequest = CompletableSubject.create();
        doReturn(tripManagerRequest).when(mockTripManager).startTrip();
        TestObserver<MapActivityUiState.State> uiStateObserver = TestObserver.create();
        mTestSubject.getUiStateChangeEventObservable().map(MapActivityUiState::getState).subscribe(uiStateObserver);

        mTestSubject.onActionButtonClicked();
        uiStateObserver.assertValue(IDLE);

        tripManagerRequest.onComplete();

        uiStateObserver.assertValues(IDLE, STARTED);
    }

    @Test
    public void onActionButtonClicked_whenIntendedToStopTrip_shouldCallTripManagerToStopTrip() {
        // Starting
        mTestSubject.onActionButtonClicked();
        // Stopping
        mTestSubject.onActionButtonClicked();

        verify(mockTripManager).finishTrip();
    }

    @Test
    public void onActionButtonClicked_whenFinishing_shouldUpdateState_whenTripManagerRequestCompletes() {
        CompletableSubject tripManagerRequest = CompletableSubject.create();
        doReturn(tripManagerRequest).when(mockTripManager).finishTrip();
        TestObserver<MapActivityUiState.State> uiStateObserver = TestObserver.create();
        mTestSubject.getUiStateChangeEventObservable().map(MapActivityUiState::getState).subscribe(uiStateObserver);

        // Starting
        mTestSubject.onActionButtonClicked();
        // Stopping
        mTestSubject.onActionButtonClicked();

        uiStateObserver.assertValues(IDLE, STARTED);

        tripManagerRequest.onComplete();

        uiStateObserver.assertValues(IDLE, STARTED, IDLE);
    }

    @Test
    public void shouldBroadcastFormattedStatistics_whenStatisticsUpdateReceived() {
        TestObserver<TripStatistics> statisticsObserver = TestObserver.create();
        mTestSubject.getTripStatisticsStreamObservable().subscribe(statisticsObserver);

        Trip trip = new Trip().updateStatistics(12.01341235, 1.0045);
        statsStream.onNext(trip);

        // 2 broadcasts happening - default during vm creation and expected one
        assertEquals(2, statisticsObserver.valueCount());

        // We are interested int the last value
        TripStatistics statistics = statisticsObserver.values().get(1);

        assertEquals("12.01", statistics.getDistance());
        assertEquals("1", statistics.getSpeed());
    }

    @Test
    public void shouldBroadcastMapOptions_whenCoordinatesUpdateReceived() {
        TestObserver<MapOptions> mapOptionsObserver = TestObserver.create();
        mTestSubject.getMapOptionsObservable().subscribe(mapOptionsObserver);

        mapOptionsObserver.assertEmpty();
        coordsStream.onNext(new TripCoordinate());
        assertEquals(1, mapOptionsObserver.valueCount());
    }

    @Test
    public void shouldBroadcastDefaultStatistics_whenCreated() {
        TestObserver<TripStatistics> statisticsObserver = TestObserver.create();
        mTestSubject.getTripStatisticsStreamObservable().subscribe(statisticsObserver);

        TripStatistics statistics = statisticsObserver.values().get(0);
        assertEquals("0", statistics.getDistance());
        assertEquals("0", statistics.getSpeed());
    }

    @Test
    public void shouldBroadcastDefaultStatistics_whenTripEnds() {
        TestObserver<TripStatistics> statisticsObserver = TestObserver.create();
        mTestSubject.getTripStatisticsStreamObservable().subscribe(statisticsObserver);

        //Starting
        mTestSubject.onActionButtonClicked();
        //Finishing
        mTestSubject.onActionButtonClicked();

        // Two values are expected - 1. emitted during creation, 2. emitted after trip ended
        assertEquals(2, statisticsObserver.valueCount());
        TripStatistics statistics = statisticsObserver.values().get(1);
        assertEquals("0", statistics.getDistance());
        assertEquals("0", statistics.getSpeed());
    }
}
