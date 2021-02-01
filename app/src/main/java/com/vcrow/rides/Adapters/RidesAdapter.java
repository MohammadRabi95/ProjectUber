package com.vcrow.rides.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vcrow.rides.Interfaces.RidesCallback;
import com.vcrow.rides.Models.RideDistance;
import com.vcrow.rides.R;

import java.util.List;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.RidesHolder> {

    private Context context;
    private List<RideDistance> list;
    private RidesCallback ridesCallback;

    public RidesAdapter(Context context, List<RideDistance> list, RidesCallback ridesCallback) {
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
        RideDistance ride = list.get(position);
        if (ride != null) {
            holder.name.setText(ride.getRide().getName());

            holder.pickup.setText(toSpannableString(context.getString(R.string.PickUp) + ride.getRide().getPickup_location()));
            holder.dropOff.setText(toSpannableString(context.getString(R.string.dropOf) + ride.getRide().getDropOff_location()));

            holder.call.setOnClickListener(view -> {
                callSelectedUser(ride.getRide().getPhone());
            });

            holder.accept.setOnClickListener(view -> {
                ridesCallback.onRideAccepted(ride.getRide());
            });

            holder.itemView.setOnClickListener(view -> {
                ridesCallback.onRideSelected(ride.getRide().getPickup_latitude(),
                        ride.getRide().getPickup_longitude(), ride.getRide().getDropOff_latitude(),
                        ride.getRide().getDropOff_longitude(), ride.getRide().getPickup_location(),
                        ride.getRide().getDropOff_location());
            });
        }
    }

    private void callSelectedUser(String phone) {
        context.startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + phone)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String toSpannableString(String text) {
        int i = text.indexOf(":") + 1;
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
