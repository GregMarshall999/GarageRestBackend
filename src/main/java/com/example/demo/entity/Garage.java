package com.example.demo.entity;

import com.example.demo.entity.embeddables.GarageFields;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Garage extends BaseEntity {
    @Embedded
    private GarageFields garageFields;

    @OneToMany(mappedBy = "garage")
    private List<Car> cars;

    public GarageFields getGarageFields() {
        return garageFields;
    }

    public void setGarageFields(GarageFields garageFields) {
        this.garageFields = garageFields;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
}
