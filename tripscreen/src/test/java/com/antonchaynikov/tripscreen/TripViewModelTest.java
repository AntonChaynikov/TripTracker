package com.antonchaynikov.tripscreen;

import com.antonchaynikov.core.authentication.Auth;
import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.model.TripCoordinate;
import com.antonchaynikov.core.data.tripmanager.TripManager;
import com.antonchaynikov.core.viewmodel.StatisticsFormatter;
import com.antonchaynikov.core.viewmodel.TripStatistics;
import com.antonchaynikov.tripscreen.uistate.TripUiState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.PublishSubject;

import static com.antonchaynikov.tripscreen.uistate.TripUiState.State.IDLE;
import static com.antonchaynikov.tripscreen.uistate.TripUiState.State.STARTED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TripViewModelTest {

    @ClassRule
    public static final RxImmediateSchedulerRule SCHEDULERS = new RxImmediateSchedulerRule();
    private TripViewModel mTestSubject;
    @Mock
    private TripManager mockTripManager;
    @Mock
    private Auth mockAuth;
    @Mock
    private StatisticsFormatter mockFormatter;

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
        doReturn(Observable.empty()).when(mockTripManager).getGeolocationAvailabilityChangeObservable();
        doReturn(new Trip()).when(mockTripManager).getCurrentTrip();
        mTestSubject = new TripViewModel(mockTripManager, mockAuth, mockFormatter, true);
    }

    @Test
    public void shouldBroadcastIdleUiState_whenJustCreated() {
        TestObserver<TripUiState.State> uiStateTestObserver = TestObserver.create();
        mTestSubject.getUiStateChangeEventObservable().map(TripUiState::getState).subscribe(uiStateTestObserver);

        uiStateTestObserver.assertValue(IDLE);
    }

    @Test
    public void shouldBroadcastRunningUIState_whenTripStarts() {
        TestObserver<TripUiState.State> uiStateTestObserver = TestObserver.create();
        mTestSubject.getUiStateChangeEventObservable().map(TripUiState::getState).subscribe(uiStateTestObserver);

        mTestSubject.onActionButtonClicked();

        // IDLE by default, then started
        uiStateTestObserver.assertValues(IDLE, STARTED);
    }

    @Test
    public void shouldBroadcastIdleUIState_whenTripStops() {
        TestObserver<TripUiState.State> uiStateTestObserver = TestObserver.create();
        mTestSubject.getUiStateChangeEventObservable().map(TripUiState::getState).subscribe(uiStateTestObserver);

        // Starts trip
        mTestSubject.onActionButtonClicked();
        // Ends trip
        mTestSubject.onActionButtonClicked();

        // IDLE by default, then started and stopped
        uiStateTestObserver.assertValues(IDLE, STARTED, IDLE);
    }

    @Test
    public void isTripStarted_shouldReturnTrue_ifTripStarted_falseOtherwise() {
        Assert.assertFalse(mTestSubject.isTripStarted());

        // Starts trip
        mTestSubject.onActionButtonClicked();
        Assert.assertTrue(mTestSubject.isTripStarted());

        // Ends trip
        mTestSubject.onActionButtonClicked();
        Assert.assertFalse(mTestSubject.isTripStarted());
    }

    @Test
    public void onActionButtonClicked_shouldAskForLocationPermission_ifNotGranted() {
        // The location permission hasn't been granted
        mTestSubject.onLocationPermissionUpdate(false);

        TestObserver<Boolean> permissionTestObserver = TestObserver.create();
        mTestSubject.getAskLocationPermissionEventObservable().subscribe(permissionTestObserver);

        mTestSubject.onActionButtonClicked();

        permissionTestObserver.assertValues(true);
    }

    @Test
    public void onActionButtonClicked_shouldNotAskForLocationPermission_ifAlreadyGranted() {
        TestObserver<Boolean> permissionTestObserver = TestObserver.create();
        mTestSubject.getAskLocationPermissionEventObservable().subscribe(permissionTestObserver);

        mTestSubject.onActionButtonClicked();

        permissionTestObserver.assertEmpty();
    }

    @Test
    public void onActionButtonClicked_shouldNotStartTrip_ifLocationPermissionNotGranted() {
        // The location permission hasn't been granted
        mTestSubject.onLocationPermissionUpdate(false);

        mTestSubject.onActionButtonClicked();

        Assert.assertFalse(mTestSubject.isTripStarted());
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
        Assert.assertEquals(1, mapOptionsTestObserver.valueCount());

        MapOptions mapOptions = mapOptionsTestObserver.values().get(0);
        Assert.assertTrue(mapOptions.shouldDeleteMarkers());
    }

    @Test
    public void onActionButtonClicked_whenStarting_shouldUpdateState_whenTripManagerRequestCompletes() {
        CompletableSubject tripManagerRequest = CompletableSubject.create();
        doReturn(tripManagerRequest).when(mockTripManager).startTrip();
        TestObserver<TripUiState.State> uiStateObserver = TestObserver.create();
        mTestSubject.getUiStateChangeEventObservable().map(TripUiState::getState).subscribe(uiStateObserver);

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
        TestObserver<TripUiState.State> uiStateObserver = TestObserver.create();
        mTestSubject.getUiStateChangeEventObservable().map(TripUiState::getState).subscribe(uiStateObserver);

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

        Trip trip = new Trip().updateStatistics(123, 321);
        TripStatistics statistics = new TripStatistics("12", "21");
        doReturn(statistics).when(mockFormatter).formatTrip(trip);
        statsStream.onNext(trip);

        // 2 broadcasts happening - default during vm creation and expected one
        Assert.assertEquals(2, statisticsObserver.valueCount());

        // We are interested int the last value
        TripStatistics receivedStatistics = statisticsObserver.values().get(1);

        Assert.assertSame(statistics, receivedStatistics);
    }

    @Test
    public void shouldBroadcastMapOptions_whenCoordinatesUpdateReceived() {
        TestObserver<MapOptions> mapOptionsObserver = TestObserver.create();
        mTestSubject.getMapOptionsObservable().subscribe(mapOptionsObserver);

        mapOptionsObserver.assertEmpty();
        double expectedLat = 123;
        double expectedLng = 321;
        coordsStream.onNext(new TripCoordinate(System.currentTimeMillis(), 123, 321));
        Assert.assertEquals(1, mapOptionsObserver.valueCount());

        MapOptions mapOptions = mapOptionsObserver.values().get(0);
        assertEquals(expectedLat, mapOptions.getCoordinatesLatitude(), 0.001);
        assertEquals(expectedLng, mapOptions.getCoordinatesLongitude(), 0.001);
        Assert.assertFalse(mapOptions.shouldDeleteMarkers());
    }

    @Test
    public void shouldBroadcastMapOptionsDeleteMarkers_whenTripEnds() {
        TestObserver<MapOptions> mapOptionsObserver = TestObserver.create();
        mTestSubject.getMapOptionsObservable().subscribe(mapOptionsObserver);

        // Start trip
        mTestSubject.onActionButtonClicked();
        // Finish trip
        mTestSubject.onActionButtonClicked();

        Assert.assertEquals(1, mapOptionsObserver.valueCount());

        MapOptions mapOptions = mapOptionsObserver.values().get(0);

        Assert.assertTrue(mapOptions.shouldDeleteMarkers());
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
        Assert.assertEquals(2, statisticsObserver.valueCount());
        TripStatistics statistics = statisticsObserver.values().get(1);
        assertEquals("0", statistics.getDistance());
        assertEquals("0", statistics.getSpeed());
    }

    @Test
    public void onStatisticsButtonClicked_shouldEmitOpenStatisticsEvent() {
        TestObserver<Boolean> eventObserver = TestObserver.create();
        mTestSubject.getGotToStatisticsObservable().subscribe(eventObserver);
        mTestSubject.onStatisticsButtonClicked();

        eventObserver.assertValue(true);
    }

    @Test
    public void onLogoutButtonClicked_shouldEmitLogoutEvent() {
        TestObserver<Boolean> eventObserver = TestObserver.create();
        mTestSubject.getLogoutObservable().subscribe(eventObserver);
        mTestSubject.onLogoutButtonClicked();

        eventObserver.assertValues(false, true);
    }

    @Test
    public void onLogoutButtonClicked_shouldFinishTrip_ifStarted() {
        // Start trip
        mTestSubject.onActionButtonClicked();
        // Logout
        mTestSubject.onLogoutButtonClicked();

        verify(mockTripManager).finishTrip();
    }

    @Test
    public void onLogoutButtonClicked_shouldEmitLogoutEvent_afterTripFinishes() {
        CompletableSubject finishTripCompletable = CompletableSubject.create();
        doReturn(finishTripCompletable).when(mockTripManager).finishTrip();

        TestObserver<Boolean> logoutEventObserver = TestObserver.create();
        mTestSubject.getLogoutObservable().subscribe(logoutEventObserver);

        // Start trip
        mTestSubject.onActionButtonClicked();
        // Logout
        mTestSubject.onLogoutButtonClicked();

        logoutEventObserver.assertValue(false);

        // Trip manager finished the trip
        finishTripCompletable.onComplete();

        logoutEventObserver.assertValues(false, true);
    }

    @Test
    public void onLogoutButtonClicked_shouldSignOut() {
        CompletableSubject finishTripCompletable = CompletableSubject.create();
        doReturn(finishTripCompletable).when(mockTripManager).finishTrip();

        // Start trip
        mTestSubject.onActionButtonClicked();
        // Logout
        mTestSubject.onLogoutButtonClicked();

        // Trip manager finished the trip
        finishTripCompletable.onComplete();

        verify(mockAuth).signOut();
    }

    @Test
    public void onLogoutButtonClicked_shouldNotSignOutIfTripNotFinished() {
        CompletableSubject finishTripCompletable = CompletableSubject.create();
        doReturn(finishTripCompletable).when(mockTripManager).finishTrip();

        // Start trip
        mTestSubject.onActionButtonClicked();
        // Logout
        mTestSubject.onLogoutButtonClicked();

        verify(mockAuth, times(0)).signOut();
    }

    @Test
    public void onLogoutButtonClicked_shouldNotFinishTrip_ifNotStarted() {
        // Logout
        mTestSubject.onLogoutButtonClicked();

        verify(mockTripManager, times(0)).finishTrip();
    }

    @Test
    public void shouldDirectToSummary_whenTripFinishes() {
        TestObserver<Long> testObserver = TestObserver.create();

        doReturn(new Trip(12345L)).when(mockTripManager).getCurrentTrip();

        mTestSubject.getProceedToSummaryObservable().subscribe(testObserver);
        // Start trip
        mTestSubject.onActionButtonClicked();
        // Finish trip
        mTestSubject.onActionButtonClicked();

        testObserver.assertValue(12345L);
    }

    // TODO implement feature
    @Test
    public void onActionButtonClicked_whenStarting_shouldStartAlarm() {
        mTestSubject.onActionButtonClicked();


    }
}
