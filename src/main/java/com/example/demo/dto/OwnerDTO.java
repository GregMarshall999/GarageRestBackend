package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OwnerDTO extends BaseDTO {
    private String name;
    private List<LocalDateTime> appointments;
    private List<Long> cars;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LocalDateTime> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<LocalDateTime> appointments) {
        this.appointments = appointments;
    }

    public List<Long> getCars() {
        return cars;
    }

    public void setCars(List<Long> cars) {
        this.cars = cars;
    }
}
