package com.example.garage.entity;

import com.example.garage.entity.enums.user.Currency;
import jakarta.persistence.Entity;

@Entity
public class Balance {
    public Long balance;

    public String bankName;

    public Currency currency;
}
