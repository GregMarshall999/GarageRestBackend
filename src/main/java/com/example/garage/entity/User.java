package com.example.garage.entity;

import com.example.garage.entity.car.Car;
import com.example.garage.entity.embeddable.user.UserCredentials;
import com.example.garage.entity.embeddable.user.UserInformation;
import com.example.garage.entity.enums.user.UserRole;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.List;

@Entity
public class User extends BaseEntity {
    private UserRole userRole;

    @Embedded
    private UserInformation userInformation;

    @Embedded
    private UserCredentials userCredentials;

    @OneToOne
    private Balance balance;

    @OneToMany
    private List<Car> cars;

    @OneToMany
    private List<Garage> garages;
}
