package com.example.demo.dto;

public class CarDTO extends BaseDTO {
    private String carName;
    private boolean isNew;
    private int cost;
    private int range;
    private String motInfo;
    private long wheelId;
    private long garageId;

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

    public long getWheelId() {
        return wheelId;
    }

    public void setWheelId(long wheelId) {
        this.wheelId = wheelId;
    }

    public long getGarageId() {
        return garageId;
    }

    public void setGarageId(long garageId) {
        this.garageId = garageId;
    }
}
