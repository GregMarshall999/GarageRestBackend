package com.example.demo.tools.autorun;

import com.example.demo.dto.CarDTO;
import com.example.demo.dto.GarageDTO;
import com.example.demo.dto.WheelDTO;
import com.example.demo.entity.Car;
import com.example.demo.entity.Garage;
import com.example.demo.entity.Wheel;
import com.example.demo.service.CRUDLService;
import com.example.demo.tools.conversion.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class ApplicationStartupRunner implements CommandLineRunner {
    @Autowired
    private Converter converter;

    @Autowired
    private CRUDLService<Car, CarDTO> carService;

    @Autowired
    private CRUDLService<Garage, GarageDTO> garageService;

    @Autowired
    private CRUDLService<Wheel, WheelDTO> wheelService;

    @Override
    public void run(String... args) throws Exception {

    }
}
