package com.antonchaynikov.tripslist;

import com.antonchaynikov.core.authentication.Auth;
import com.antonchaynikov.core.data.model.Trip;
import com.antonchaynikov.core.data.repository.Repository;
import com.antonchaynikov.core.viewmodel.StatisticsFormatter;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

public class TripsListViewModelTest {

    @ClassRule
    public static final RxImmediateSchedulerRule SCHEDULERS = new RxImmediateSchedulerRule();

    private TripsListViewModel mTestSubject;

    @Mock
    private Repository mockRepository;
    @Mock
    private Auth mockAuth;
    @Mock
    private StatisticsFormatter mockStatisticsFormatter;

    private PublishSubject<List<Trip>> tripPublishSubject = PublishSubject.create();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doReturn(tripPublishSubject).when(mockRepository).getAllTrips();
        doReturn(true).when(mockAuth).isSignedIn();
        mTestSubject = new TripsListViewModel(mockRepository, mockAuth, mockStatisticsFormatter);
    }

    @Test
    public void shouldBroadcastEmptyListEvent_ifNoTrips() {
        TestObserver<Boolean> emptyListEventObserver = TestObserver.create();
        mTestSubject.getEmptyListEventObservable().subscribe(emptyListEventObserver);
        mTestSubject.onStart();
        tripPublishSubject.onNext(new ArrayList<>(0));

        emptyListEventObserver
                .assertValue(true)
                .dispose();
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
    public void shouldBroadcastTripDataLoaded_whenListLoads() {
        TestObserver<Boolean> tripListObserver = TestObserver.create();
        mTestSubject.getTripsDataLoadedEventObservable().subscribe(tripListObserver);
        mTestSubject.onStart();
        tripPublishSubject.onNext(Collections.singletonList(new Trip()));

        tripListObserver.assertValue(true);
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
