package com.example.garage.entity.car;

import com.example.garage.entity.embeddable.car.DragRating;
import com.example.garage.entity.embeddable.car.Overview;
import com.example.garage.entity.embeddable.car.TechnicalSpecification;
import com.example.garage.entity.embeddable.car.Tuning;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Car {
    private String name;
    private String description;

    @Embedded
    private Overview overview;

    @Embedded
    private TechnicalSpecification technicalSpecification;

    @Embedded
    private Tuning tuning;

    @Embedded
    private DragRating dragRating;

    @OneToMany
    private List<Car> variants;
}
