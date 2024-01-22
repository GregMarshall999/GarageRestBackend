package com.example.garage.entity.embeddable.car;

import jakarta.persistence.Embeddable;

@Embeddable
public class Tuning {
    private String power;
    private String Torque;

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getTorque() {
        return Torque;
    }

    public void setTorque(String torque) {
        Torque = torque;
    }
}
