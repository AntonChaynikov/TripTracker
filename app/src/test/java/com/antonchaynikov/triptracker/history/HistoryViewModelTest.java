package com.antonchaynikov.triptracker.history;

import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.model.TripCoordinate;
import com.antonchaynikov.triptracker.data.repository.Repository;
import com.antonchaynikov.triptracker.viewmodel.StatisticsFormatter;
import com.antonchaynikov.triptracker.viewmodel.TripStatistics;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

public class HistoryViewModelTest {

    private static final long START_DATE = 0;

    private HistoryViewModel mTestSubject;
    @Mock
    private Repository mockRepository;
    @Mock
    private StatisticsFormatter mockStatisticsFormatter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(Observable.empty()).when(mockRepository).getTripByStartDate(anyLong());
        doReturn(Observable.empty()).when(mockRepository).getCoordinatesForTrip(START_DATE);
        doReturn(new TripStatistics("0", "0")).when(mockStatisticsFormatter).formatTrip(any(Trip.class));
        mTestSubject = new HistoryViewModel(mockRepository, mockStatisticsFormatter, START_DATE);
    }

    @Test
    public void onStart_shouldEmitTripStats() {
        Trip trip = new Trip(START_DATE);
        TripStatistics statistics = TripStatistics.getDefaultStatistics();

        TestObserver<TripStatistics> statisticsObserver = TestObserver.create();

        doReturn(Observable.just(trip)).when(mockRepository).getTripByStartDate(START_DATE);
        doReturn(statistics).when(mockStatisticsFormatter).formatTrip(trip);

        mTestSubject.getStatisticsObservable().subscribe(statisticsObserver);
        mTestSubject.onStart();

        statisticsObserver.assertValue(statistics);
    }

    @Test
    public void onStart_shouldEmitRoute() {
        PublishSubject<List<TripCoordinate>> coordinatesStream = PublishSubject.create();

        doReturn(Observable.just(Collections.singletonList(new TripCoordinate())))
                .when(mockRepository)
                .getCoordinatesForTrip(START_DATE);

        TestObserver<MapOptions> mapOptionsObserver = TestObserver.create();

        mTestSubject.getMapOptionsObservable().subscribe(mapOptionsObserver);
        mTestSubject.onStart();

        assertEquals(1, mapOptionsObserver.valueCount());
    }

    @Test
    public void onStart_shouldShowProgressBar_whenTripDataLoading() {
        PublishSubject<Trip> tripDataStream = PublishSubject.create();
        doReturn(tripDataStream).when(mockRepository).getTripByStartDate(START_DATE);
        TestObserver<Boolean> progressBarObserver = TestObserver.create();

        mTestSubject.getShowProgressBarEventBroadcast().subscribe(progressBarObserver);
        mTestSubject.onStart();

        // Data is loading, should show progress bar
        progressBarObserver.assertValue(true);

        // Data has been loaded
        tripDataStream.onNext(new Trip());

        // Progress bar should be hidden now
        progressBarObserver.assertValues(true, false);
    }
}