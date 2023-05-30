package com.example.demo.entity.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CarInfo {
    @Column(nullable = false)
    private int cost;
    @Column(name = "car_range")
    private int range;
    @Column(nullable = false)
    private String motInfo;

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getMotInfo() {
        return motInfo;
    }

    public void setMotInfo(String motInfo) {
        this.motInfo = motInfo;
    }
}
