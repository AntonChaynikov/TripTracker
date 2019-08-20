package com.antonchaynikov.tripslist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class TripsAdapter extends RecyclerView.Adapter<TripsViewHolder> {

    private TripsProvider mTripsProvider;
    private ItemClickListener mClickListener;

    TripsAdapter(@NonNull TripsProvider tripsProvider, @NonNull ItemClickListener clickListener) {
        mTripsProvider = tripsProvider;
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TripsViewHolder(inflater.inflate(R.layout.layout_trip_item, parent, false), mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TripsViewHolder holder, int position) {
        holder.bind(mTripsProvider.getTrip(position));
    }

    @Override
    public int getItemCount() {
        return mTripsProvider.getItemCount();
    }

    interface ItemClickListener {
        void onItemClicked(int position);
    }

    interface TripsProvider {
        TripsListItemModel getTrip(int position);

        int getItemCount();
    }
}
