package com.antonchaynikov.tripslist;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antonchaynikov.core.data.model.Trip;

import java.util.Locale;

class TripsViewHolder extends RecyclerView.ViewHolder {

    private TextView tvDate;
    private TextView tvDistance;
    private TextView tvSpeed;

    public TripsViewHolder(@NonNull View itemView) {
        super(itemView);
        tvDate = itemView.findViewById(R.id.tv_trip_item_date);
        tvDistance = itemView.findViewById(R.id.tv_trip_item_distance);
        tvSpeed = itemView.findViewById(R.id.tv_trip_item_speed);
    }

    void bind(@NonNull Trip trip) {
        tvDate.setText(String.format(Long.toString(trip.getStartDate()), Locale.getDefault()));
        tvDistance.setText(String.format(Double.toString(trip.getDistance()), Locale.getDefault()));
        tvSpeed.setText(String.format(Double.toString(trip.getSpeed()), Locale.getDefault()));
    }
}
