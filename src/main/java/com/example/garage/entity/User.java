package com.example.garage.entity;

import com.example.garage.entity.enums.Civility;
import com.example.garage.entity.enums.UserRole;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class User extends BaseEntity {
    private String firstname;
    private String surname;
    private String userName;
    private String email;

    private Civility civility;
    private UserRole userRole;

    private List<Car> cars;
}
