package com.vcrow.rides.Models;

public class Car {
    private String id;
    private String driverId;
    private String model;
    private String color;
    private String number;
    private String company;
    private String name;
    private String licenseImageURl;
    private String carImageURL;
    private String licenseNumber;

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseImageURl() {
        return licenseImageURl;
    }

    public void setLicenseImageURl(String licenseImageURl) {
        this.licenseImageURl = licenseImageURl;
    }

    public String getCarImageURL() {
        return carImageURL;
    }

    public void setCarImageURL(String carImageURL) {
        this.carImageURL = carImageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
