package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OwnerDTO extends BaseDTO {
    private String name;
    private List<LocalDateTime> appointments;
    private List<Long> carIds;
    private List<Long> ownerIds;

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

    public List<Long> getCarIds() {
        return carIds;
    }

    public void setCarIds(List<Long> carIds) {
        this.carIds = carIds;
    }

    public List<Long> getOwnerIds() {
        return ownerIds;
    }

    public void setOwnerIds(List<Long> ownerIds) {
        this.ownerIds = ownerIds;
    }
}
