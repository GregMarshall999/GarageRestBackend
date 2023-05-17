package com.example.demo.controller;

import com.example.demo.dto.CarDTO;
import com.example.demo.entity.Car;
import com.example.demo.repository.ICarRepository;
import com.example.demo.service.ICRUDLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController extends AbstractCRUDLController<Car, CarDTO> {
    @Autowired
    public CarController(ICRUDLService<Car, CarDTO> service, ICarRepository repository) {
        super(service, Car.class, CarDTO.class, repository);
    }
}
