package com.antonchaynikov.triptracker;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import io.reactivex.functions.Consumer;

public class Mapper {

    public final static float DEFAULT_ZOOM_LEVEL = 18;

    public Mapper(TrackCalculator trackCalculator) {
        mTrackCalculator = trackCalculator;
        mTrackCalculator.getTrackBroadcast().subscribe(new Consumer<PolylineOptions>() {
            @Override
            public void accept(PolylineOptions polylineOptions) throws Exception {
                if (mGoogleMap != null) {
                    mGoogleMap.addPolyline(polylineOptions);
                }
            }
        });
    }

    private GoogleMap mGoogleMap;
    private TrackCalculator mTrackCalculator;
    private boolean mIsTrackingRoute;

    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    public void clear() {
        mGoogleMap.clear();
    }

    public void addMarker(LatLng latLng) {
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
        );
    }

    public void startRouteTrack() {
        mIsTrackingRoute = true;
    }

    public void stopRouteTrack() {
        mIsTrackingRoute = false;
    }

    public void moveCamera(LatLng latLng) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL));
    }

    public void moveCamera(LatLng latLng, float zoom) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void addToPath(Location location) {
        mTrackCalculator.addLocation(location);
    }

    public void clearPath() {

    }

    public boolean isReady() {
        return mGoogleMap != null;
    }

}
