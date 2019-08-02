package com.android.ran.model;

import java.io.Serializable;

public class Cab implements Serializable {

    private boolean isShared;
    private boolean isBooked;
    private String collegeName;
    private String pickup;
    private String drop;
    private String startTime;
    private String endTime;
    private String seats;
    private String carName;
    private String carNumber;
    private String driver;
    private String fare;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    private String _id;

    public Cab(String collegeName, String pickup, String drop, String startTime, String endTime,
               String seats, String carName, String carNumber, String driver, String fare) {
        this.isShared = false;
        this.isBooked = false;
        this.collegeName = collegeName;
        this.pickup = pickup;
        this.drop = drop;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seats = seats;
        this.carName = carName;
        this.carNumber = carNumber;
        this.driver = driver;
        this.fare = fare;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean isShared) {
        this.isShared = isShared;
    }

    public boolean isBooked() {
        return isShared;
    }

    public void setBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }
}
