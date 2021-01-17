package com.example.projectuber.Models;

public class Rides {

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

    public Rides() {
    }

    public Rides(String id, String userId, String name,
                 String pickup_location, String pickup_latitude,
                 String pickup_longitude, String phone,
                 String dropOff_location, String dropOff_latitude,
                 String dropOff_longitude) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.pickup_location = pickup_location;
        this.pickup_latitude = pickup_latitude;
        this.pickup_longitude = pickup_longitude;
        this.phone = phone;
        this.dropOff_location = dropOff_location;
        this.dropOff_latitude = dropOff_latitude;
        this.dropOff_longitude = dropOff_longitude;
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
