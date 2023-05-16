package com.example.demo.controller;

import com.example.demo.dto.CarDTO;
import com.example.demo.entity.Car;
import com.example.demo.service.ICRUDLService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController extends AbstractCRUDLController<Car, CarDTO> {
    public CarController(ICRUDLService<Car, CarDTO> service) {
        super(service);
    }
}
