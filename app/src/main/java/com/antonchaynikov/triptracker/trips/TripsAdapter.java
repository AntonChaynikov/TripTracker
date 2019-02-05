package com.antonchaynikov.triptracker.trips;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.antonchaynikov.triptracker.R;
import com.antonchaynikov.triptracker.data.model.Trip;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class TripsAdapter extends RecyclerView.Adapter<TripsViewHolder> {

    private List<Trip> mTrips;

    TripsAdapter(@NonNull List<Trip> trips) {
        mTrips = trips;
    }

    @NonNull
    @Override
    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TripsViewHolder(inflater.inflate(R.layout.layout_trip_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TripsViewHolder holder, int position) {
        holder.bind(mTrips.get(position));
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    interface ItemClickListener {

        void onItemClicked(int position);

    }

}
