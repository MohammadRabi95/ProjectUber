package com.example.projectuber.Interfaces;

import com.example.projectuber.Models.Rides;

public interface RidesCallback {
    void onRideAccepted(Rides rides);
    void onRideSelected(String pickupLat, String pickupLng,
                        String dropOffLat, String dropOffLng,
                        String pickupLocation, String dropOffLocation);

}
