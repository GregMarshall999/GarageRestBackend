package com.example.demo.entity.embeddables;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class GarageFields {
    @Embedded
    private GarageAccounting garageAccounting;

    private String ownerName;

    public GarageAccounting getGarageAccounting() {
        return garageAccounting;
    }

    public void setGarageAccounting(GarageAccounting garageAccounting) {
        this.garageAccounting = garageAccounting;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
