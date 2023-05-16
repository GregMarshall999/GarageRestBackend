package com.example.demo.controller;

import com.example.demo.dto.GarageDTO;
import com.example.demo.entity.Garage;
import com.example.demo.service.ICRUDLService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/garages")
public class GarageController extends AbstractCRUDLController<Garage, GarageDTO> {
    public GarageController(ICRUDLService<Garage, GarageDTO> service) {
        super(service);
    }
}
