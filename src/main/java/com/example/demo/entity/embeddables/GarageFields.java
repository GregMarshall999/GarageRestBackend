package com.example.demo.entity.embeddables;

import jakarta.persistence.Embeddable;

@Embeddable
public class GarageFields {
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
