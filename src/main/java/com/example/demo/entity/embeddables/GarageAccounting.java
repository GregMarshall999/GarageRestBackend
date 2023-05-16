package com.example.demo.entity.embeddables;

import jakarta.persistence.Embeddable;

@Embeddable
public class GarageAccounting {
    private String ref;
    private int cost;
    private int income;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }
}
