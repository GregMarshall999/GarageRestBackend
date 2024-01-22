package com.example.garage.entity.component;

import com.example.garage.entity.enums.car.WeightUnit;

public class Weight {
    private String value;
    private WeightUnit unit;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public WeightUnit getUnit() {
        return unit;
    }

    public void setUnit(WeightUnit unit) {
        this.unit = unit;
    }
}
