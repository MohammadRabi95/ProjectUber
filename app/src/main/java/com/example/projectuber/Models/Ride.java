package com.example.projectuber.Models;

import java.io.Serializable;

public class Ride implements Serializable {

    private String id;
    private String userId;
    private String name;
    private String pickup_location;
    private String pickup_latitude;
    private String pickup_longitude;
    private String phone;
    private String dropOff_location;
    private String dropOff_latitude;
    private String dropOff_longitude;
    private String distance;
    private String duration;
    private String price;

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

    public Ride() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

}
