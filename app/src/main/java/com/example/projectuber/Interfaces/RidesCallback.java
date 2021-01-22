package com.example.projectuber.Interfaces;

import com.example.projectuber.Models.Ride;

public interface RidesCallback {
    void onRideAccepted(Ride ride);
    void onRideSelected(String pickupLat, String pickupLng,
                        String dropOffLat, String dropOffLng,
                        String pickupLocation, String dropOffLocation);

}
