package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Wheel extends BaseEntity {
    @Column(nullable = false)
    private int price;
    private String brand;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
