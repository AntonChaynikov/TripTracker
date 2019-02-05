package com.antonchaynikov.triptracker.trips;

import com.antonchaynikov.triptracker.RxImmediateSchedulerRule;
import com.antonchaynikov.triptracker.data.model.Trip;
import com.antonchaynikov.triptracker.data.repository.Repository;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class TripsListViewModelTest {

    @ClassRule
    public static final RxImmediateSchedulerRule SCHEDULERS = new RxImmediateSchedulerRule();

    private TripsListViewModel mTestSubject;

    @Mock
    private Repository mockRepository;

    private PublishSubject<List<Trip>> tripPublishSubject = PublishSubject.create();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(tripPublishSubject).when(mockRepository).getAllTrips();
        mTestSubject = new TripsListViewModel(mockRepository);
    }

    @Test
    public void shouldBroadcastEmptyListEvent_ifNoTrips() {
        TestObserver<Boolean> emptyListEventObserver = TestObserver.create();
        mTestSubject.getEmptyListEventObservable().subscribe(emptyListEventObserver);
        mTestSubject.onStart();
        tripPublishSubject.onNext(new ArrayList<>(0));

        emptyListEventObserver.assertValue(true);
    }

    @Test
    public void shouldNotBroadcastEmptyListEvent_ifTripsPresent() {
        TestObserver<Boolean> emptyListEventObserver = TestObserver.create();
        mTestSubject.getEmptyListEventObservable().subscribe(emptyListEventObserver);
        mTestSubject.onStart();
        tripPublishSubject.onNext(Collections.singletonList(new Trip()));

        emptyListEventObserver.assertEmpty();
    }

    @Test
    public void shouldBroadcastTripsList_whenListLoads() {
        TestObserver<List<Trip>> tripListObserver = TestObserver.create();
        mTestSubject.getTripListObservable().subscribe(tripListObserver);
        mTestSubject.onStart();
        tripPublishSubject.onNext(Collections.singletonList(new Trip()));

        List<Trip> trips = tripListObserver.values().get(0);
        assertEquals(1, trips.size());
    }

    @Test
    public void shouldShowProgressBar_whenDataStartloading() {
        TestObserver<Boolean> progressBarObserver = TestObserver.create();
        mTestSubject.getShowProgressBarEventBroadcast().subscribe(progressBarObserver);

        // data starts to load
        mTestSubject.onStart();

        // have to show progress bar
        progressBarObserver.assertValue(true);

        // loading completed
        tripPublishSubject.onNext(Collections.singletonList(new Trip()));

        // have to hide shown progress bar
        progressBarObserver.assertValues(true, false);
    }
}
