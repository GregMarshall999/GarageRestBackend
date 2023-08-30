package com.example.demo.entity.embeddables;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class GarageFields {
    @Embedded
    private GarageAccounting garageAccounting;

    public GarageAccounting getGarageAccounting() {
        return garageAccounting;
    }

    public void setGarageAccounting(GarageAccounting garageAccounting) {
        this.garageAccounting = garageAccounting;
    }
}
