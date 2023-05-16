package com.example.demo.dto;

public class CarDTO extends BaseDTO {
    private String carName;
    private boolean isNew;
    private int cost;
    private int range;
    private String motInfo;
    private WheelDTO wheel;
    private GarageDTO garage;

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

    public WheelDTO getWheel() {
        return wheel;
    }

    public void setWheel(WheelDTO wheel) {
        this.wheel = wheel;
    }

    public GarageDTO getGarage() {
        return garage;
    }

    public void setGarage(GarageDTO garage) {
        this.garage = garage;
    }
}
