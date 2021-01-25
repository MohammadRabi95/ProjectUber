package com.example.projectuber.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuber.Models.CompletedRide;
import com.example.projectuber.R;

import java.util.List;

public class CompletedRidesAdapter extends RecyclerView.Adapter<CompletedRidesAdapter.CompletedRidesHolder> {

    private List<CompletedRide> list;
    private Context context;

    public CompletedRidesAdapter(List<CompletedRide> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CompletedRidesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CompletedRidesHolder(LayoutInflater.from(context)
                .inflate(R.layout.completed_rides_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedRidesHolder holder, int position) {
        CompletedRide completedRide = list.get(position);

        if (completedRide != null) {
            holder.textView.setText("Driver Name: " + completedRide.getDriverName() +
                    "\nPassenger Name: " + completedRide.getPassengerName());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CompletedRidesHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public CompletedRidesHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.a);
        }
    }
}
