package com.example.garage.entity.embeddable.car;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Year;

@Embeddable
public class Overview {
    @Column(nullable = false)
    public String carBrand;

    @Column(nullable = false)
    public Year production;

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public Year getProduction() {
        return production;
    }

    public void setProduction(Year production) {
        this.production = production;
    }
}
