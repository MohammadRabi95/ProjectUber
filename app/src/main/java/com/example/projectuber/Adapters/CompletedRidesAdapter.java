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
import com.example.projectuber.Utils.Constants;

import java.util.List;

import static com.example.projectuber.Utils.Constants.DRIVER;
import static com.example.projectuber.Utils.Constants.PASSENGER;

public class CompletedRidesAdapter extends RecyclerView.Adapter<CompletedRidesAdapter.CompletedRidesHolder> {

    private List<CompletedRide> list;
    private Context context;
    private int type;

    public CompletedRidesAdapter(List<CompletedRide> list, Context context, int type) {
        this.list = list;
        this.context = context;
        this.type = type;
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
            if (type == DRIVER) {
                holder._name.setText("Passenger Name:");
                holder._id.setText("Passenger ID:");
                holder.name.setText(completedRide.getPassengerName());
                holder.id.setText(completedRide.getPassengerId());
            } else if (type == PASSENGER) {
                holder._name.setText("Driver Name:");
                holder._id.setText("Driver ID:");
                holder.name.setText(completedRide.getDriverName());
                holder.id.setText(completedRide.getDriverId());
            }
            holder.p_loc.setText(completedRide.getPickup_location());
            holder.d_loc.setText(completedRide.getDropOff_location());
            holder.amount.setText(completedRide.getPrice());
            holder.paid_via.setText(completedRide.getPaidVia());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CompletedRidesHolder extends RecyclerView.ViewHolder {
        TextView name, _name, _id, id, amount, p_loc, d_loc, paid_via;

        public CompletedRidesHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.p_name);
            _name = itemView.findViewById(R.id.Name);
            _id = itemView.findViewById(R.id.id);
            id = itemView.findViewById(R.id.p_id);
            amount = itemView.findViewById(R.id.price);
            p_loc = itemView.findViewById(R.id.p_loc);
            d_loc = itemView.findViewById(R.id.d_loc);
            paid_via = itemView.findViewById(R.id.p_via);


        }
    }
}
