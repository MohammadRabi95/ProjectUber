package com.example.projectuber.CompletedRides;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectuber.Adapters.CompletedRidesAdapter;
import com.example.projectuber.DatabaseCalls;
import com.example.projectuber.Interfaces.ResponseInterface;
import com.example.projectuber.Models.CompletedRide;
import com.example.projectuber.R;
import com.example.projectuber.Utils.AppHelper;

import java.util.List;

import static com.example.projectuber.Utils.Constants.PASSENGER;

public class AsPassengerFragment extends Fragment {

    public AsPassengerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.completed_ride_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        DatabaseCalls.getMyCompletedRidesCall(getContext(), "passengerId", new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                List<CompletedRide> list = (List<CompletedRide>) params[0];
                recyclerView.setAdapter(new CompletedRidesAdapter(list, getContext(), PASSENGER));
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(view.findViewById(android.R.id.content), error);
            }
        });
        return view;
    }
}