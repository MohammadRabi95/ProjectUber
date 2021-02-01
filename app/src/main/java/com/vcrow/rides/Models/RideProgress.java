package com.vcrow.rides.Models;

import java.io.Serializable;

public class RideProgress implements Serializable {
    private String id;
    private String passengerId;
    private String driverId;
    private String passengerName;
    private String driverName;
    private String pickup_location;
    private String pickup_latitude;
    private String pickup_longitude;
    private String driverPhone;
    private String passengerPhone;
    private String dropOff_location;
    private String dropOff_latitude;
    private String dropOff_longitude;
    private String rideAcceptedTimeStamp;
    private String pickupTimeStamp;
    private String distance;
    private String duration;
    private String price;
    private boolean isRideStarted;

    public RideProgress() {
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isRideStarted() {
        return isRideStarted;
    }

    public void setRideStarted(boolean rideStarted) {
        isRideStarted = rideStarted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getPickup_latitude() {
        return pickup_latitude;
    }

    public void setPickup_latitude(String pickup_latitude) {
        this.pickup_latitude = pickup_latitude;
    }

    public String getPickup_longitude() {
        return pickup_longitude;
    }

    public void setPickup_longitude(String pickup_longitude) {
        this.pickup_longitude = pickup_longitude;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public String getDropOff_location() {
        return dropOff_location;
    }

    public void setDropOff_location(String dropOff_location) {
        this.dropOff_location = dropOff_location;
    }

    public String getDropOff_latitude() {
        return dropOff_latitude;
    }

    public void setDropOff_latitude(String dropOff_latitude) {
        this.dropOff_latitude = dropOff_latitude;
    }

    public String getDropOff_longitude() {
        return dropOff_longitude;
    }

    public void setDropOff_longitude(String dropOff_longitude) {
        this.dropOff_longitude = dropOff_longitude;
    }

    public String getRideAcceptedTimeStamp() {
        return rideAcceptedTimeStamp;
    }

    public void setRideAcceptedTimeStamp(String rideAcceptedTimeStamp) {
        this.rideAcceptedTimeStamp = rideAcceptedTimeStamp;
    }

    public String getPickupTimeStamp() {
        return pickupTimeStamp;
    }

    public void setPickupTimeStamp(String pickupTimeStamp) {
        this.pickupTimeStamp = pickupTimeStamp;
    }
}
