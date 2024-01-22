package com.example.garage.entity.embeddable.user;

import jakarta.persistence.Embeddable;

@Embeddable
public class UserCredentials {
    private String userName;
    private String email;
    private String password;
}
