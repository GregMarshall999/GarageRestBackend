package com.example.garage.entity.embeddable.car;

import jakarta.persistence.Embeddable;

@Embeddable
public class DragRating {
    private String factory;
    private String tuning;

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getTuning() {
        return tuning;
    }

    public void setTuning(String tuning) {
        this.tuning = tuning;
    }
}
