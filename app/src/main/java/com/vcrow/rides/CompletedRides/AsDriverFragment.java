package com.vcrow.rides.CompletedRides;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vcrow.rides.Adapters.CompletedRidesAdapter;
import com.vcrow.rides.DatabaseCalls;
import com.vcrow.rides.Interfaces.ResponseInterface;
import com.vcrow.rides.Models.CompletedRide;
import com.vcrow.rides.R;
import com.vcrow.rides.Utils.AppHelper;

import java.util.List;

import static com.vcrow.rides.Utils.Constants.DRIVER;

public class AsDriverFragment extends Fragment {


    public AsDriverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.completed_ride_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        DatabaseCalls.getMyCompletedRidesCall(getContext(), "driverId", new ResponseInterface() {
            @Override
            public void onResponse(Object... params) {
                List<CompletedRide> list = (List<CompletedRide>) params[0];
                recyclerView.setAdapter(new CompletedRidesAdapter(list, getContext(), DRIVER));
            }

            @Override
            public void onError(String error) {
                AppHelper.showSnackBar(view.findViewById(android.R.id.content), error);
            }
        });
        return view;
    }
}