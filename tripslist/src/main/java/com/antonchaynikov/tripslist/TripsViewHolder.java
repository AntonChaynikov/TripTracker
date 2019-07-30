package com.antonchaynikov.tripslist;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class TripsViewHolder extends RecyclerView.ViewHolder {

    private TextView tvDate;
    private TextView tvDistance;
    private TextView tvSpeed;

    public TripsViewHolder(@NonNull View itemView, @NonNull TripsAdapter.ItemClickListener clickListener) {
        super(itemView);
        itemView.setOnClickListener(v -> {
            clickListener.onItemClicked(getLayoutPosition());
        });
        tvDate = itemView.findViewById(R.id.tv_trip_item_date);
        tvDistance = itemView.findViewById(R.id.tv_trip_item_distance);
        tvSpeed = itemView.findViewById(R.id.tv_trip_item_speed);
    }

    void bind(@NonNull TripsListItemModel trip) {
        tvDate.setText(trip.getDate());
        tvDistance.setText(trip.getDistance());
        tvSpeed.setText(trip.getSpeed());
    }
}
