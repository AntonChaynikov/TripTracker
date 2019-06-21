package com.antonchaynikov.tripshistory;

import androidx.annotation.NonNull;

import com.antonchaynikov.core.data.model.TripCoordinate;
import com.antonchaynikov.core.utils.CollectionUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Objects;

public class MapOptions {

    private MarkerOptions mMarkerOptions;
    private PolylineOptions mPolylineOptions;
    private CollectionUtils.Converter<TripCoordinate, LatLng> mConverter;

    public MapOptions(@NonNull List<TripCoordinate> tripCoordinates) {
        this();
        if (tripCoordinates.isEmpty()) {
            mMarkerOptions = new MarkerOptions(null);
        } else {
            mMarkerOptions = new MarkerOptions(tripCoordinates.get(tripCoordinates.size() - 1));
        }
        mPolylineOptions.addAll(CollectionUtils.map(tripCoordinates, mConverter));
    }

    public MapOptions() {
        mPolylineOptions = new PolylineOptions();
        mConverter = (tripCoordinate) -> new LatLng(tripCoordinate.getLatitude(), tripCoordinate.getLongitude());
    }

    public void addCoordinate(@NonNull TripCoordinate coordinate) {
        mPolylineOptions.add(mConverter.convert(coordinate));
        mMarkerOptions = new MarkerOptions(coordinate);
    }

    public MarkerOptions getMarkerOptions() {
        return mMarkerOptions;
    }

    public PolylineOptions getPolylineOptions() {
        return mPolylineOptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapOptions)) return false;
        MapOptions that = (MapOptions) o;
        return Objects.equals(mMarkerOptions, that.mMarkerOptions) &&
                Objects.equals(mPolylineOptions, that.mPolylineOptions) &&
                Objects.equals(mConverter, that.mConverter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mMarkerOptions, mPolylineOptions, mConverter);
    }
}
