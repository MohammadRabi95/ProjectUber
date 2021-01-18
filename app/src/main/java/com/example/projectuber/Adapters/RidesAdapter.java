package com.example.projectuber.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectuber.Interfaces.RidesCallback;
import com.example.projectuber.Models.Rides;
import com.example.projectuber.R;

import java.util.List;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.RidesHolder> {

    private Context context;
    private List<Rides> list;
    private RidesCallback ridesCallback;

    public RidesAdapter(Context context, List<Rides> list, RidesCallback ridesCallback) {
        this.context = context;
        this.list = list;
        this.ridesCallback = ridesCallback;
    }

    @NonNull
    @Override
    public RidesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RidesHolder(LayoutInflater.from(context)
                .inflate(R.layout.rides_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RidesHolder holder, int position) {
        Rides rides = list.get(position);
        if (rides != null) {
            holder.name.setText(rides.getName());

            holder.pickup.setText(toSpannableString(context.getString(R.string.PickUp) + rides.getPickup_location()));
            holder.dropOff.setText(toSpannableString(context.getString(R.string.dropOf) + rides.getDropOff_location()));

            // holder.pickup.setText(toSpannableString("Pickup: " + rides.getPickup_location()));
           // holder.dropOff.setText(toSpannableString("DropOff: " + rides.getDropOff_location()));

            holder.call.setOnClickListener(view -> {
                callSelectedUser(rides.getPhone());
            });

            holder.accept.setOnClickListener(view -> {
                ridesCallback.onRideAccepted(rides);
            });

            holder.itemView.setOnClickListener(view -> {
                ridesCallback.onRideSelected(rides.getPickup_latitude(),
                        rides.getPickup_longitude(), rides.getDropOff_latitude(),
                        rides.getDropOff_longitude(), rides.getPickup_location(),
                        rides.getDropOff_location());
            });
        }
    }

    private void callSelectedUser(String phone) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String toSpannableString(String text) {
        int i = text.indexOf(":") + 1;
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),0, i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString.toString();
    }

    static class RidesHolder extends RecyclerView.ViewHolder {
        TextView name, pickup, dropOff;
        Button call, accept;

        public RidesHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_rides_row);
            pickup = itemView.findViewById(R.id.pickup_rides_row);
            dropOff = itemView.findViewById(R.id.dropOff_rides_row);
            call = itemView.findViewById(R.id.rides_call_row);
            accept = itemView.findViewById(R.id.accept_call_row);
        }
    }
}
