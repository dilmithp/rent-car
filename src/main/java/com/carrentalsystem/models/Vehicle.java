package com.carrentalsystem.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Vehicle {
    private int vehicleId;
    private String vehicleNumber;
    private String brand;
    private String model;
    private String vehicleType;
    private int year;
    private String color;
    private BigDecimal dailyRate;
    private String availabilityStatus;
    private Timestamp createdAt;

    public Vehicle() {
    }

    public Vehicle(int vehicleId, String vehicleNumber, String brand, String model,
                   String vehicleType, int year, String color, BigDecimal dailyRate,
                   String availabilityStatus, Timestamp createdAt) {
        this.vehicleId = vehicleId;
        this.vehicleNumber = vehicleNumber;
        this.brand = brand;
        this.model = model;
        this.vehicleType = vehicleType;
        this.year = year;
        this.color = color;
        this.dailyRate = dailyRate;
        this.availabilityStatus = availabilityStatus;
        this.createdAt = createdAt;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", year=" + year +
                ", color='" + color + '\'' +
                ", dailyRate=" + dailyRate +
                ", availabilityStatus='" + availabilityStatus + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
