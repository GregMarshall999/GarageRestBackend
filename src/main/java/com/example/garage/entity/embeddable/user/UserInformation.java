package com.example.garage.entity.embeddable.user;

import com.example.garage.entity.enums.user.Civility;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserInformation {
    private String firstname;
    private String surname;

    private Civility civility;


}
