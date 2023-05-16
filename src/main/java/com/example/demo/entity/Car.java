package com.example.demo.entity;

import com.example.demo.entity.embeddables.CarFields;
import com.example.demo.entity.embeddables.CarInfo;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Car extends BaseEntity {
    @Embedded
    private CarFields carFields;

    @Embedded
    private CarInfo carInfo;

    @ManyToOne
    private Wheel wheel;

    @ManyToOne
    private Garage garage;

    public CarFields getCarFields() {
        return carFields;
    }

    public void setCarFields(CarFields carFields) {
        this.carFields = carFields;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(CarInfo carInfo) {
        this.carInfo = carInfo;
    }

    public Wheel getWheel() {
        return wheel;
    }

    public void setWheel(Wheel wheel) {
        this.wheel = wheel;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }
}
