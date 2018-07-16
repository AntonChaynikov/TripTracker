package com.antonchaynikov.triptracker;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Deque;
import java.util.LinkedList;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class TrackCalculator {

    private final static int MIN_DISTANCE_THRESHOLD_METERS = 60;
    private final static float DEFAULT_ACCURACY = 100;

    private BehaviorSubject<PolylineOptions> mTrackBroadcast = BehaviorSubject.create();

    private double mAverageAccuracy = Double.NaN;

    private Deque<Location> mLocationsQueue = new LinkedList<>();

    public void addLocation(Location location) {
        updateAverageAccuracy(location);
        if (isLikelyMoving(location) || mLocationsQueue.isEmpty()) {
            mLocationsQueue.add(location);
            broadcastTrack();
        }
    }

    public Observable<PolylineOptions> getTrackBroadcast() {
        return mTrackBroadcast;
    }


    private void broadcastTrack() {
        Location loc2 = mLocationsQueue.pollLast();
        Location loc1 = mLocationsQueue.peekLast();
        mLocationsQueue.add(loc2);
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(getLatLng(loc1))
                .add(getLatLng(loc2));
        mTrackBroadcast.onNext(polylineOptions);
    }

    private LatLng getLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void updateAverageAccuracy(Location location) {
        float accuracy;
        if (!location.hasAccuracy()) {
            accuracy = DEFAULT_ACCURACY;
        } else {
            accuracy = location.getAccuracy();
        }
        if (mAverageAccuracy == Double.NaN) {
            mAverageAccuracy = accuracy;
        } else {
            mAverageAccuracy = (mAverageAccuracy + accuracy) / 2;
        }
    }

    private float getAccuracy(Location location) {
        return location.hasAccuracy()? location.getAccuracy() : DEFAULT_ACCURACY;
    }

    private boolean isLikelyMoving(Location location) {
        if (mLocationsQueue.isEmpty()) {
            return false;
        }
        Location lastRecordedLocation = mLocationsQueue.peekLast();
        return lastRecordedLocation.distanceTo(location) >= mAverageAccuracy * 2;
    }
}
