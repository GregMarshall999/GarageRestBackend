package com.example.demo.dto;

import java.io.Serializable;

public abstract class BaseDTO implements Serializable {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
