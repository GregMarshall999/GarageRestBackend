package com.example.demo.entity.embeddables;

import jakarta.persistence.Embeddable;

@Embeddable
public class CarFields {
    private String carName;
    private boolean isNew;

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
